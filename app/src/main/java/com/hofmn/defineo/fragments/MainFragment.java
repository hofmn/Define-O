package com.hofmn.defineo.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hofmn.defineo.R;
import com.hofmn.defineo.TrainingActivity;
import com.hofmn.defineo.data.FetchWordsTask;
import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainFragment extends Fragment {

    public static final int STATS_FRAGMENTS_COUNT = 3;
    private final String DATA_KEY = "data";
    ArrayList<WordData> data;
    private String[] titles = {"Сьогодні", "Вчора", "Місяць"};

    public MainFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        if (data == null) {
            data = new ArrayList<WordData>();
            FetchWordsTask fetchWordsTask = new FetchWordsTask();
            try {
                data = fetchWordsTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().setTitle("Статистика");

        TextView startTextView = (TextView) rootView.findViewById(R.id.startTrainingTextView);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        startTextView.setTypeface(typeface);

        final Intent intent = new Intent(getActivity(), TrainingActivity.class);
        intent.putExtra(DATA_KEY, data);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(intent);
            }
        });

        ((ViewPager) rootView.findViewById(R.id.statsPager))
                .setAdapter(new StatsPagerAdapter(getChildFragmentManager()));

        return rootView;
    }

    private class StatsPagerAdapter extends FragmentPagerAdapter {
        public StatsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StatsFragment.newInstance(new int[]{10, 20, 70});
                case 1:
                    return StatsFragment.newInstance(new int[]{33, 33, 34});
                case 2:
                    return StatsFragment.newInstance(new int[]{42, 42, 12});
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return STATS_FRAGMENTS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
