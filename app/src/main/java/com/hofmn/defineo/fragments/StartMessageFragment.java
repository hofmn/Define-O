package com.hofmn.defineo.fragments;


import android.graphics.Typeface;
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
public class StartMessageFragment extends Fragment {

    public StartMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_start_message, container, false);

        ((TextView) rootView.findViewById(R.id.trainingStartedWelcome))
                .setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                        "fonts/Roboto-Medium.ttf"));

        return rootView;
    }
}
