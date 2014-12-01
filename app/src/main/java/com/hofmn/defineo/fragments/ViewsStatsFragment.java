package com.hofmn.defineo.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.hofmn.defineo.R;
import com.hofmn.defineo.WordsManager;
import com.hofmn.defineo.data.model.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewsStatsFragment extends Fragment {

    private TextView wordsViewsTextView;
    private TextView definitionsViewsTextView;
    private TextView translationsViewsTextView;
    private PieChart pieChart;

    private DatabaseHandler databaseHandler;

    private int wordsViews;
    private int definitionsViews;
    private int translationsViews;

    public ViewsStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_views_stats, container, false);

        pieChart = (PieChart) rootView.findViewById(R.id.chart);

        databaseHandler = new DatabaseHandler(getActivity());

        wordsViewsTextView = (TextView) rootView.findViewById(R.id.allWordsCountTextView);
        definitionsViewsTextView = (TextView) rootView.findViewById(R.id.repeatCountTextView);
        translationsViewsTextView = (TextView) rootView.findViewById(R.id.translationViews);

        return rootView;
    }

    private void updateUI() {
        setStatsViewsValues();
        pieChart.setData(generatePieData(getStatsPercentageArray()));
        pieChart.setDrawXValues(false);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setTouchEnabled(false);
        pieChart.setDrawLegend(false);
        pieChart.setDrawCenterText(false);
        pieChart.setDrawUnitsInChart(false);
        pieChart.setDescription("");
        pieChart.notifyDataSetChanged();
        pieChart.refreshDrawableState();
        pieChart.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Light.ttf"));
        pieChart.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return "" + (int) v;
            }
        });
        pieChart.animateY(1500);

        wordsViewsTextView.setText("" + wordsViews);
        definitionsViewsTextView.setText("" + definitionsViews);
        translationsViewsTextView.setText("" + translationsViews);

        WordsManager.getInstance().clearStatsViews();
    }

    private void setStatsViewsValues() {
        wordsViews = databaseHandler.getViewStatsValue(DatabaseHandler.STATS_WORD_VIEWS);
        definitionsViews = databaseHandler
                .getViewStatsValue(DatabaseHandler.STATS_DEFINITION_VIEWS);
        translationsViews = databaseHandler
                .getViewStatsValue(DatabaseHandler.STATS_TRANSLATION_VIEWS);
    }

    private float[] getStatsPercentageArray() {
        float[] percentages = new float[3];

        int viewsSum = wordsViews + definitionsViews + translationsViews;

        if (viewsSum > 0) {
            percentages[0] = (100 * wordsViews) / viewsSum;
            percentages[1] = (100 * definitionsViews) / viewsSum;
            percentages[2] = (100 * translationsViews) / viewsSum;
            int percentagesSum = (int) (percentages[0] + percentages[1] + percentages[2]);
            int randomIndex = new Random().nextInt((2) + 1);
            switch (percentagesSum) {
                case 99:
                    percentages[randomIndex] += 1;
                    break;
                case 98:
                    percentages[randomIndex] += 2;
                    break;
            }
            return percentages;
        } else {
            return new float[]{0, 0, 0};
        }
    }

    private PieData generatePieData(float[] data) {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < data.length; i++) {
            xValues.add("entry" + (i + 1));

            entries.add(new Entry(data[i], i));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{R.color.button_text_color,
                        R.color.accent_color_three,
                        R.color.accent_color_two},
                getActivity());
        dataSet.setSliceSpace(2f);

        return new PieData(xValues, dataSet);
    }

}
