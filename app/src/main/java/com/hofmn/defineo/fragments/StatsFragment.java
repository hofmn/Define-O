package com.hofmn.defineo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.hofmn.defineo.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment {

    public static final String STATS_KEY = "stats";

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(int[] statsValues) {
        StatsFragment fragment = new StatsFragment();

        Bundle args = new Bundle();
        args.putIntArray(STATS_KEY, statsValues);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        PieChart pieChart = (PieChart) rootView.findViewById(R.id.chart);
        pieChart.setData(generatePieData(getArguments().getIntArray(STATS_KEY)));
        pieChart.setDrawXValues(false);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setTouchEnabled(false);
        pieChart.setDrawLegend(false);
        pieChart.setDrawCenterText(false);
        pieChart.setDrawUnitsInChart(false);
        pieChart.setDescription("");

        return rootView;
    }

    private PieData generatePieData(int[] data) {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < data.length; i++) {
            xValues.add("entry" + (i + 1));

            entries.add(new Entry(data[i], i));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{R.color.accent_color_one,
                        R.color.accent_color_two,
                        R.color.button_text_color},
                getActivity());
        dataSet.setSliceSpace(2f);

        return new PieData(xValues, dataSet);
    }

}
