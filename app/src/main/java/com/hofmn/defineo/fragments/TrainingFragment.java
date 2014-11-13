package com.hofmn.defineo.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hofmn.defineo.DefineoApp;
import com.hofmn.defineo.R;
import com.hofmn.defineo.TrainingActivity;
import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class TrainingFragment extends Fragment {

    public static final int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech textToSpeech;

    private ArrayList<String> words;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int pagesCount;

    private boolean canSpeak;

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Тренування");
        initializeTextToSpeech();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mPagerAdapter = new WordSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i > 0) {
                    speakWord(words.get(i - 1));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        ((TrainingActivity) getActivity()).getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.primary_color)));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (i == TextToSpeech.SUCCESS) {
                                    textToSpeech.setLanguage(Locale.US);
                                    canSpeak = true;
                                } else if (i == TextToSpeech.ERROR) {
                                    Toast.makeText(getActivity(), "Text To Speech failed...",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }).start();
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
        ArrayList<WordData> data = DefineoApp.getInstance().getData();

        words = new ArrayList<String>();
        for (WordData dataObject : data) {
            words.add(dataObject.getWord().getWord());
        }
        pagesCount = words.size() + 1;
        Collections.shuffle(words);
    }

    private void initializeTextToSpeech() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        Log.d("TrainingFragment", "initTTS");
    }

    private void speakWord(String word) {
        if (textToSpeech != null && canSpeak) {
            //textToSpeech.setSpeechRate(0.5f); // funny voice :)
            textToSpeech.setPitch(1.3f);
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class WordSlidePagerAdapter extends FragmentStatePagerAdapter {
        public WordSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StartMessageFragment();
                default:
                    return WordCardFragment.newInstance(words.get(position - 1));
            }
        }

        @Override
        public int getCount() {
            return pagesCount;
        }
    }
}
