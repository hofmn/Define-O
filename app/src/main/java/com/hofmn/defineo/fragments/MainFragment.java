package com.hofmn.defineo.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hofmn.defineo.R;
import com.hofmn.defineo.TrainingActivity;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        PieChart pieChart = (PieChart) rootView.findViewById(R.id.chart);
        pieChart.setData(generatePieData());
        pieChart.setDrawXValues(false);
        pieChart.setDrawLegend(false);
        pieChart.setDrawCenterText(false);
        pieChart.setDrawUnitsInChart(false);
        pieChart.setDescription("");

        TextView startTextView = (TextView) rootView.findViewById(R.id.startTrainingTextView);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        startTextView.setTypeface(typeface);

        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),
                        TrainingActivity.class));
            }
        });

        return rootView;
    }

    private PieData generatePieData() {

        int count = 3;

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            xValues.add("entry" + (i + 1));

            entries.add(new Entry((float) (Math.random() * 60) + 40, i));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setSliceSpace(2f);

        return new PieData(xValues, dataSet);
    }
}
