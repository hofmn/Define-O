package com.hofmn.defineo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hofmn.defineo.DefineoApp;
import com.hofmn.defineo.R;
import com.hofmn.defineo.adapters.TranslationAdapter;
import com.hofmn.defineo.data.model.Translation;
import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslationFragment extends Fragment {

    public TranslationFragment() {
        // Required empty public constructor
    }

    public static TranslationFragment newInstance(String word) {
        TranslationFragment fragment = new TranslationFragment();

        Bundle args = new Bundle();
        args.putString(WordCardFragment.WORD_KEY, word);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_translation, container, false);

        String word = getArguments().getString(WordCardFragment.WORD_KEY);

        ArrayList<Translation> translations = getTranslationForWord(word);

        ListView listView = (ListView) rootView.findViewById(R.id.translationsListView);
        listView.setAdapter(new TranslationAdapter(getActivity(), translations));

        ((TextView) rootView.findViewById(R.id.wordToTranslateTextView)).setText(word);

        return rootView;
    }

    private ArrayList<Translation> getTranslationForWord(String word) {
        ArrayList<WordData> data = DefineoApp.getInstance().getData();
        for (WordData wordData : data) {
            if (wordData.getWord().getWord().equals(word)) {
                return wordData.getTranslations();
            }
        }
        return null;
    }


}
