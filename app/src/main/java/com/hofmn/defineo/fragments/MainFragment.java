package com.hofmn.defineo.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hofmn.defineo.MainActivity;
import com.hofmn.defineo.R;
import com.hofmn.defineo.SettingsActivity;
import com.hofmn.defineo.TrainingActivity;
import com.hofmn.defineo.WordsManager;
import com.hofmn.defineo.data.model.Definition;
import com.hofmn.defineo.data.model.Translation;
import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;
import com.hofmn.defineo.data.model.db.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainFragment extends Fragment {

    public static final int STATS_FRAGMENTS_COUNT = 2;
    public static final int MY_DATA_CHECK_CODE = 0;
    protected ArrayList<WordData> data;
    protected DatabaseHandler databaseHandler;
    private RelativeLayout contentLayout;
    private RelativeLayout noWordsLayout;
    private ProgressBar progressBar;

    private TextToSpeech textToSpeech;

    private String[] titles = {"ПОТОЧНІ РЕЗУЛЬТАТИ", "ПЕРЕГЛЯДИ"};

    public MainFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        setRepeatTime();

        databaseHandler = new DatabaseHandler(getActivity());

        boolean dbExist = DatabaseHandler.doesDatabaseExist(getActivity(),
                DatabaseHandler.getDbName());

        if (!dbExist) {
            data = new ArrayList<WordData>();
            new FetchWordsTask().execute();
        } else {
            data = new ArrayList<WordData>(databaseHandler.getAllWords());
            if (data.size() == 0) {
                contentLayout.setVisibility(View.GONE);
                noWordsLayout.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).getSupportActionBar().hide();
            }
            WordsManager.getInstance().setData(data);
            Log.d("MainFragment", String.valueOf(data.size()));
        }


        if (textToSpeech == null) {
            new InitTtsTask().execute();
        }

        WordsManager.getInstance().clearLearningPhaseMap();

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().setTitle("Статистика");

        progressBar = (ProgressBar) rootView.findViewById(R.id.loadingProgress);
        contentLayout = (RelativeLayout) rootView.findViewById(R.id.mainContentLayout);
        noWordsLayout = (RelativeLayout) rootView.findViewById(R.id.noWordsLayout);

        TextView startTextView = (TextView) rootView.findViewById(R.id.startTrainingTextView);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        startTextView.setTypeface(typeface);

        final Intent intent = new Intent(getActivity(), TrainingActivity.class);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(intent);
            }
        });

        ((ViewPager) rootView.findViewById(R.id.statsPager))
                .setAdapter(new StatsPagerAdapter(getChildFragmentManager()));

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int i) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (i == TextToSpeech.SUCCESS) {
                                    textToSpeech.setLanguage(Locale.US);
                                    WordsManager.getInstance().setTTS(textToSpeech);
                                    contentLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else if (i == TextToSpeech.ERROR) {
                                    Toast.makeText(getActivity(), "Text To Speech failed...",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }
                });
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private void setRepeatTime() {

        float repeatTime = Float.parseFloat(PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(SettingsActivity.KEY_REPEAT_FREQUENCY, "6"));

        Log.d("MF", "REPEAT TIME " + repeatTime);

        WordsManager.getInstance().setRepeatFrequency(repeatTime);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }

    private class StatsPagerAdapter extends FragmentPagerAdapter {
        public StatsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ProgressStatsFragment();
                case 1:
                    return new ViewsStatsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return STATS_FRAGMENTS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public class FetchWordsTask extends AsyncTask<String, Void, ArrayList<WordData>> {

        private final String LOG_TAG = FetchWordsTask.class.getSimpleName();

        @Override
        protected ArrayList<WordData> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String wordsJsonStr;

            try {
                String downloadJsonUrl = "https://www.dropbox.com/s/fx42mcf7u2qi5lt/words.json?dl=1";
                URL url = new URL(downloadJsonUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) {
                    return null;
                }

                wordsJsonStr = builder.toString();

                try {
                    return getWordsDataFromJson(wordsJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error: " + e.getMessage());
                }

                Log.d(LOG_TAG, wordsJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<WordData> wordDataArrayList) {
            if (wordDataArrayList != null) {
                data = wordDataArrayList;
                databaseHandler.insertWords(data);
                databaseHandler.insertViewStatsValues();
                WordsManager.getInstance().setData(data);
            } else {
                Log.d(LOG_TAG, "wordDataArrayList == NULL");
            }
        }

        private ArrayList<WordData> getWordsDataFromJson(String wordsJsonStr) throws JSONException {

            final String WORDS_ARRAY = "words";
            final String WORD_KEY = "word";
            final String DEFINITION_ARRAY = "definitions";
            final String DEFINITION_KEY = "definition";
            final String CONTEXT_KEY = "context";
            final String TRANSLATIONS_ARRAY = "translations";
            final String TRANSLATION_KEY = "translation";

            ArrayList<WordData> data = new ArrayList<WordData>();

            Word word;

            JSONObject wordsDataJson = new JSONObject(wordsJsonStr);
            JSONArray wordsArray = wordsDataJson.getJSONArray(WORDS_ARRAY);

            for (int i = 0; i < wordsArray.length(); i++) {
                JSONObject wordObject = wordsArray.getJSONObject(i);
                word = new Word(wordObject.getString(WORD_KEY));
                JSONArray definitionArray = wordObject.getJSONArray(DEFINITION_ARRAY);

                ArrayList<Definition> definitions = new ArrayList<Definition>();
                ArrayList<Translation> translations = new ArrayList<Translation>();

                for (int j = 0; j < definitionArray.length(); j++) {
                    JSONObject definitionObject = definitionArray.getJSONObject(j);
                    String definition = definitionObject.getString(DEFINITION_KEY);
                    String context = definitionObject.getString(CONTEXT_KEY);
                    definitions.add(new Definition(definition, context));
                }

                JSONArray translationArray = wordObject.getJSONArray(TRANSLATIONS_ARRAY);

                for (int k = 0; k < translationArray.length(); k++) {
                    JSONObject translationObject = translationArray.getJSONObject(k);
                    String translation = translationObject.getString(TRANSLATION_KEY);
                    translations.add(new Translation(translation));
                }

                Log.d("Fetch", String.valueOf(definitions.size()));
                data.add(new WordData(word, definitions, translations));
            }

            return data;
        }
    }

    private class InitTtsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
            Log.d("MainFragment", "initTTS");
            return null;
        }
    }
}
