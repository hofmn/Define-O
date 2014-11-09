package com.hofmn.defineo.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hofmn.defineo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordCardFragment extends Fragment {

    public static final String WORD_KEY = "word";
    private String word;

    private OnWordClick callback;

    public WordCardFragment() {
        // Required empty public constructor
    }

    public static WordCardFragment newInstance(String word) {
        WordCardFragment fragment = new WordCardFragment();

        Bundle args = new Bundle();
        args.putString(WORD_KEY, word);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (OnWordClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnWordClick");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_word_card, container, false);

        word = getArguments().getString(WORD_KEY);

        rootView.findViewById(R.id.word_card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onWordClick(word);
            }
        });

        ((TextView) rootView.findViewById(R.id.wordTextView)).setText(word);

        return rootView;
    }


    public interface OnWordClick {
        void onWordClick(String word);
    }
}
