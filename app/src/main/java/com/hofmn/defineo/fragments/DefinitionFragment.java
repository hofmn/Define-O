package com.hofmn.defineo.fragments;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hofmn.defineo.R;
import com.hofmn.defineo.adapters.DefinitionAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefinitionFragment extends Fragment {

    private String word;
    private ArrayList<String> definitions;

    private OnShowTranslationClicked callback;

    public DefinitionFragment() {
        // Required empty public constructor
    }

    public static DefinitionFragment newInstance(String word) {
        DefinitionFragment fragment = new DefinitionFragment();

        Bundle args = new Bundle();
        args.putString(WordCardFragment.WORD_KEY, word);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (OnShowTranslationClicked) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnShowTranslationClicked");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_definition, container, false);

        word = getArguments().getString(WordCardFragment.WORD_KEY);

        definitions = new ArrayList<String>();

        for (int i = 0; i < 2; i++) {
            definitions.add("This is definition for word " + word);
        }

        ListView definitionListView = (ListView) rootView.findViewById(R.id.definitionsListView);
        definitionListView.setAdapter(new DefinitionAdapter(getActivity(), definitions));

        ((TextView) rootView.findViewById(R.id.wordToDefineTextView)).setText(word);

        TextView showTranslation = (TextView) rootView.findViewById(R.id.showTranslationTextView);
        showTranslation.setTypeface(Typeface.createFromAsset(getActivity()
                .getAssets(), "fonts/Roboto-Bold.ttf"));

        showTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onShowTranslation(word);
            }
        });

        return rootView;
    }

    public interface OnShowTranslationClicked {
        void onShowTranslation(String word);
    }
}
