package com.hofmn.defineo.fragments;


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
public class TranslateFragment extends Fragment {


    public TranslateFragment() {
        // Required empty public constructor
    }

    public static TranslateFragment newInstance(String word) {
        TranslateFragment fragment = new TranslateFragment();

        Bundle args = new Bundle();
        args.putString(WordCardFragment.WORD_KEY, word);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);

        String word = getArguments().getString(WordCardFragment.WORD_KEY);

        String translation = "This is translation for word " + word;

        ((TextView) rootView.findViewById(R.id.wordToTranslateTextView)).setText(word);
        ((TextView) rootView.findViewById(R.id.translationTextView)).setText(translation);

        return rootView;
    }


}
