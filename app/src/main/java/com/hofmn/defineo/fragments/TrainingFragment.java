package com.hofmn.defineo.fragments;

import android.graphics.drawable.ColorDrawable;
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

import com.hofmn.defineo.R;
import com.hofmn.defineo.TrainingActivity;
import com.hofmn.defineo.WordsManager;
import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;
import com.hofmn.defineo.data.model.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;

public class TrainingFragment extends Fragment {

    DatabaseHandler databaseHandler;
    private TextToSpeech textToSpeech;
    private ArrayList<String> wordsList;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int pagesCount;
    private String lastWord;

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Тренування");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        textToSpeech = WordsManager.getInstance().getTextToSpeech();

        if (wordsList == null) {
            initializeWordsList();
        }

        databaseHandler = new DatabaseHandler(getActivity());

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new WordSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i != 0 && i < pagesCount - 1) {
                    WordsManager.getInstance().updateStatsWordViews(true);
                    speakWord(wordsList.get(i - 1));
                    lastWord = wordsList.get(i - 1);
                }

                if (i > 1 && i < pagesCount) {
                    String word = wordsList.get(i - 2);
                    WordsManager.getInstance().addToLearningPhaseMap(word,
                            Word.LearningPhase.Repeat);
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

    private void initializeWordsList() {
        ArrayList<WordData> data = WordsManager.getInstance().getData();

        wordsList = new ArrayList<String>();

        for (WordData dataObject : data) {
            wordsList.add(dataObject.getWord().getWord());
        }

        pagesCount = wordsList.size() + 2;
        Collections.shuffle(wordsList);
    }

    private void speakWord(String word) {
        if (textToSpeech != null && !word.equals(lastWord)) {
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class WordSlidePagerAdapter extends FragmentStatePagerAdapter {
        public WordSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new StartMessageFragment();
            } else if (position == getCount() - 1) {
                return new EndMessageFragment();
            } else {
                return WordCardFragment.newInstance(wordsList.get(position - 1));
            }
        }

        @Override
        public int getCount() {
            return pagesCount;
        }
    }
}
