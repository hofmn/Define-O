package com.hofmn.defineo;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class MainFragment extends Fragment {

    public static final int MY_DATA_CHECK_CODE = 0;
    private static int wordIndex = 0;
    private TextSwitcher wordTextSwitcher;
    private ArrayList<String> words;
    private TextToSpeech textToSpeech;

    private String engWord;
    private String ukrWord;

    private Animation flipIn;
    private Animation flipOut;
    private Animation swipeIn;
    private Animation swipeOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeTextToSpeech();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initializeWordsList();

        final String startText = "Tap to start";

        wordTextSwitcher = (TextSwitcher) view.findViewById(R.id.wordTextView);

        initializeAnimations();

        setWordTextSwitcher(AnimationType.Swipe);

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

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {

            @Override
            public void onSwipeTop() {
                updateWordTextView(true);
            }

            @Override
            public void onSwipeRight() {
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeBottom() {
                updateWordTextView(true);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

            @Override
            public void onTap() {
                updateWordTextView(false);
            }

            @Override
            public void onLongTap() {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String keyword = "define " + engWord;
                intent.putExtra(SearchManager.QUERY, keyword);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.shutdown();
    }

    private void updateWordTextView(boolean shouldTranslate) {

        setWordTextSwitcher(shouldTranslate ? AnimationType.Flip : AnimationType.Swipe);

        if (shouldTranslate) {
            ukrWord = words.get(wordIndex).split(getString(R.string.words_separator))[1];
            engWord = words.get(wordIndex).split(getString(R.string.words_separator))[0];
            String currentWord = (String) ((TextView) wordTextSwitcher.getCurrentView()).getText();
            wordTextSwitcher.setText(currentWord.equals(ukrWord) ? engWord : ukrWord);
        } else {
            if (wordIndex < words.size()) {
                wordIndex++;
            } else {
                Collections.shuffle(words);
                wordIndex = 0;
            }

            engWord = words.get(wordIndex).split(getString(R.string.words_separator))[0];
            wordTextSwitcher.setText(engWord);
            speakWord(engWord);
        }

        // TODO: Remove this log
        Log.d("MainFragment", engWord + " : " + ukrWord);
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

    private void initializeWordsList() {
        String[] stringArray = getResources().getStringArray(R.array.popular_english_verbs);
        words = new ArrayList<String>(Arrays.asList(stringArray));
        Collections.shuffle(words);
    }

    private void initializeTextToSpeech() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    private void initializeAnimations() {
        swipeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        swipeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);

        flipIn = AnimationUtils.loadAnimation(getActivity(), R.anim.grow);
        flipOut = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);
    }

    private void setWordTextSwitcher(AnimationType animationType) {
        switch (animationType) {
            case Flip:
                wordTextSwitcher.setInAnimation(flipIn);
                wordTextSwitcher.setOutAnimation(flipOut);
                break;
            case Swipe:
                wordTextSwitcher.setInAnimation(swipeIn);
                wordTextSwitcher.setOutAnimation(swipeOut);
                break;
        }
    }
}
