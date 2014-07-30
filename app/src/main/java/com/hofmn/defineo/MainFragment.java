package com.hofmn.defineo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class MainFragment extends Fragment {

    private TextSwitcher wordTextSwitcher;
    private ArrayList<String> words;

    private static int wordIndex = 0;
    public static final int MY_DATA_CHECK_CODE = 0;

    private TextToSpeech textToSpeech;

    public static final String EXTRA_WORDS_LIST = "list";
    public static final String EXTRA_INDEX = "index";

    @Override
    public void onStart() {
        super.onStart();
        initializeTextToSpeech();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initializeWordsList(savedInstanceState);

        final String startText = (savedInstanceState == null)
                ? "Tap to start"
                : words.get(savedInstanceState.getInt(EXTRA_INDEX) - 1);

        wordTextSwitcher = (TextSwitcher) view.findViewById(R.id.wordTextView);

        Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);

        wordTextSwitcher.setInAnimation(in);
        wordTextSwitcher.setOutAnimation(out);

        wordTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView myText = new TextView(getActivity());
                myText.setText(startText);
                myText.setGravity(Gravity.CENTER);
                myText.setTextSize(30);
                myText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                myText.setTextColor(getResources().getColor(R.color.color_clouds));
                return myText;
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWordTextView();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.shutdown();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(EXTRA_WORDS_LIST, words);
        outState.putInt(EXTRA_INDEX, wordIndex);
    }

    private void updateWordTextView() {
        String word;

        if (wordIndex < words.size()) {
            word = words.get(wordIndex++);
            wordTextSwitcher.setText(word);
        } else {
            Collections.shuffle(words);
            wordIndex = 0;
            word = words.get(wordIndex);
            wordTextSwitcher.setText(word);
        }

        speakWord(word);
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
//            textToSpeech.setSpeechRate(0.3f); // funny voice :)
            textToSpeech.setPitch(1.1f);
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {
                            textToSpeech.setLanguage(Locale.US);
                        } else if (i == TextToSpeech.ERROR) {
                            Toast.makeText(getActivity(), "Text To Speech failed...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private void initializeWordsList(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            String[] stringArray = getResources().getStringArray(R.array.popular_english_verbs);
            words = new ArrayList<String>(Arrays.asList(stringArray));
            Collections.shuffle(words);
        } else {
            words = savedInstanceState.getStringArrayList(EXTRA_WORDS_LIST);
        }
    }

    private void initializeTextToSpeech() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }
}
