package com.hofmn.defineo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hofmn.defineo.R;
import com.hofmn.defineo.WordsManager;
import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ProgressStatsFragment extends Fragment {

    public static final String STATS_KEY = "progress stats";

    private TextView allWordsCountTextView;
    private TextView toRepeatCountTextView;
    private TextView toLearnCountTextView;

    public ProgressStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress_stats, container, false);
        allWordsCountTextView = (TextView) rootView.findViewById(R.id.allWordsCountTextView);
        toRepeatCountTextView = (TextView) rootView.findViewById(R.id.repeatCountTextView);
        toLearnCountTextView = (TextView) rootView.findViewById(R.id.learnCountTextView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int[] progressValues = getProgressStatsValues();

        Log.d("PSF", "onRESUME");
        allWordsCountTextView.setText(String.valueOf(progressValues[0]));
        toRepeatCountTextView.setText(String.valueOf(progressValues[1]));
        toLearnCountTextView.setText(String.valueOf(progressValues[2]));
    }

    private int[] getProgressStatsValues() {
        int[] progressValues = new int[3];

        ArrayList<WordData> data = WordsManager.getInstance().getData();

        int allWords = data.size();
        int toRepeat = 0;
        int toLearn = 0;

        for (WordData wordData : data) {
            if (wordData.getWord().getLearningPhase() == Word.LearningPhase.Repeat) {
                toRepeat++;
            } else {
                toLearn++;
            }
        }

        Log.d("MF", "all words: " + allWords + " repeat: " + toRepeat + " learn: " + toLearn);

        progressValues[0] = allWords;
        progressValues[1] = toRepeat;
        progressValues[2] = toLearn;

        return progressValues;
    }
}
