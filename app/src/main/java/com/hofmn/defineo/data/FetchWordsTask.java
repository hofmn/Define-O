package com.hofmn.defineo.data;

import android.os.AsyncTask;
import android.util.Log;

import com.hofmn.defineo.DefineoApp;
import com.hofmn.defineo.data.model.Definition;
import com.hofmn.defineo.data.model.Translation;
import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;

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

public class FetchWordsTask extends AsyncTask<String, Void, ArrayList<WordData>> {

    private final String LOG_TAG = FetchWordsTask.class.getSimpleName();

    @Override
    protected ArrayList<WordData> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String wordsJsonStr;

        try {
            String downloadJsonUrl = "https://www.dropbox.com/s/9pydseu6tjkpeq2/test?dl=1";
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
            Log.d(LOG_TAG, String.valueOf(wordDataArrayList.size()));
            DefineoApp.getInstance().setData(wordDataArrayList);
        }
    }

    private ArrayList<WordData> getWordsDataFromJson(String wordsJsonStr) throws JSONException {

        final String WORDS_ARRAY = "words";
        final String WORD_KEY = "word";
        final String DEFINITION_ARRAY = "definitions";
        final String DEFINITION_KEY = "definition";
        final String CONTEXT_KEY = "context";
        final String TYPE_KEY = "type";
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
                String type = definitionObject.getString(TYPE_KEY);
                definitions.add(new Definition(definition, context, type));
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
