package com.hofmn.defineo.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hofmn.defineo.R;
import com.hofmn.defineo.TrainingActivity;
import com.hofmn.defineo.WordsManager;
import com.hofmn.defineo.adapters.DefinitionAdapter;
import com.hofmn.defineo.data.model.Definition;
import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;

public class DefinitionFragment extends Fragment {

    private String word;
    private ArrayList<Definition> definitions;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        getActivity().setTitle("Визначення слова");

        ((TrainingActivity) getActivity())
                .getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03A9F4")));

        definitions = getDefinitionsForWord(word);

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

    private ArrayList<Definition> getDefinitionsForWord(String word) {
        ArrayList<WordData> data = WordsManager.getInstance().getData();
        for (WordData wordData : data) {
            if (wordData.getWord().getWord().equals(word)) {
                return wordData.getDefinitions();
            }
        }
        return null;
    }

    public interface OnShowTranslationClicked {
        void onShowTranslation(String word);
    }
}
