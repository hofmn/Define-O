package com.hofmn.defineo.fragments;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hofmn.defineo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class TrainingFragment extends Fragment {

    public static final int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech textToSpeech;

    private ArrayList<String> words;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int pagesCount;

    @Override
    public void onStart() {
        super.onStart();
        initializeTextToSpeech();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        if (textToSpeech == null) {
            initializeTextToSpeech();
        }

        if (words == null) {
            initializeWordsList();
        }

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i > 0) {
                    String word = words.get(i - 1)
                            .split(getString(R.string.words_separator))[0];
                    word = word.substring(0, word.length() - 1);
                    speakWord(word);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return view;
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
                            Toast.makeText(getActivity(), "Text To Speech failed...",
                                    Toast.LENGTH_SHORT)
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

    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.shutdown();
    }

    private void initializeWordsList() {
        String[] stringArray = getResources().getStringArray(R.array.popular_english_verbs);
        words = new ArrayList<String>(Arrays.asList(stringArray));
        pagesCount = words.size();
        Collections.shuffle(words);
    }

    private void initializeTextToSpeech() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
            //textToSpeech.setSpeechRate(0.5f); // funny voice :)
            textToSpeech.setPitch(1.3f);
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StartMessageFragment();
                default:
                    return WordCardFragment.newInstance(words.get(position - 1)
                            .split(getString(R.string.words_separator))[0]);
            }
        }

        @Override
        public int getCount() {
            return pagesCount;
        }
    }
}
