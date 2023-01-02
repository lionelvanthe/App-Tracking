package com.example.apptracking.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.example.apptracking.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

public class HourlyChart {

    private RadarChart radarChart;
    private List<Float> listData;
    private Context context;

    public HourlyChart(RadarChart radarChart,List<Float> listData, Context context) {
        this.radarChart = radarChart;
        this.listData = listData;
        this.context = context;

        setupHourlyBreakdown();
    }

    private void setupHourlyBreakdown() {
        radarChart.setBackgroundColor(Color.rgb(38, 36, 47));
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.WHITE);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.WHITE);
        radarChart.setWebAlpha(100);
        radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad);
        radarChart.getLegend().setEnabled(false);

        setupXAxis();
        setupYAxis();

    }
    private void setupXAxis() {
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final String[] mActivities = context.getResources().getStringArray(R.array.hours_daily);

            @Override
            public String getFormattedValue(float value) {
                if (value/2 < mActivities.length) {
                    if (value%2 == 0) {
                        return mActivities[(int) (value/2)];
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            }
        });
        xAxis.setTextColor(Color.WHITE);
    }

    private void setupYAxis() {
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setLabelCount(5, true);
        yAxis.setDrawLabels(false);
    }

    public void setDataForHourlyChart() {

        ArrayList<RadarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            entries.add(new RadarEntry(listData.get(i) + 20));
        }

        RadarDataSet dataSet = new RadarDataSet(entries, "");
        dataSet.setColor(Color.rgb(133,147,189));
        dataSet.setFillColor(Color.rgb(81,86,116));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(180);
        dataSet.setLineWidth(1.2f);
        dataSet.setDrawHighlightCircleEnabled(true);
        dataSet.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(dataSet);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        radarChart.setData(data);
        radarChart.invalidate();
    }
}
