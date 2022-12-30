package com.example.apptracking.ui.fragment.appdetail;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.FragmentAppDetailBinding;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.custom.MyAxisTitleFormatter;
import com.example.apptracking.ui.custom.MyAxisValueFormatter;
import com.example.apptracking.utils.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppDetailFragment extends BaseBindingFragment<FragmentAppDetailBinding, AppDetailViewModel> {

    private App app;

    @Override
    protected Class<AppDetailViewModel> getViewModel() {
        return AppDetailViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        app = AppDetailFragmentArgs.fromBundle(getArguments()).getApp();

        binding.tvUsageContent.setText(Utils.formatMilliSeconds(app.getUsageTimeOfDay()));
        binding.tvOpenContent.setText(String.valueOf(app.getTimesOpened()));
        binding.tvAverageContent.setText(Utils.formatMilliSeconds(app.getUsageTimeOfDay()/24));
        binding.tvNotificationContent.setText(String.valueOf(viewModel.getNotificationReceive(app.getPackageName())));

        try {
            long timestampInstall = requireActivity().getPackageManager().getPackageInfo(app.getPackageName(), 0 ).firstInstallTime;
            binding.tvInstallationDateContent.setText(Utils.converterTimestampToDate(timestampInstall));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        CopyOnWriteArrayList<Float> arrayList = new CopyOnWriteArrayList<>(app.getUsageTimePerHour());
        binding.barView.setDataList(arrayList, 60);
        setupSessionLengthChart(app.getMapSessionsLength());
        setupHourlyBreakdown(app.getUsageTimePerHour());

    }

    private void setupSessionLengthChart(HashMap<String, Integer> mapSessionsLength) {
        binding.chartSessionLength.setDrawBarShadow(false);
        binding.chartSessionLength.setDrawValueAboveBar(true);
        binding.chartSessionLength.getDescription().setEnabled(false);
        binding.chartSessionLength.setMaxVisibleValueCount(60);
        binding.chartSessionLength.setPinchZoom(false);
        binding.chartSessionLength.setDrawGridBackground(false);
        binding.chartSessionLength.getLegend().setEnabled(false);

        setupXAxis();
        setupYAxis();

        binding.chartSessionLength.setFitBars(true);
        binding.chartSessionLength.animateY(1000);

        setDataForSessionsLengthChart(mapSessionsLength);
    }

    private void setupHourlyBreakdown(Float[] usageTimePerHour) {
        binding.chartHourlyBreakdown.setBackgroundColor(Color.rgb(38, 36, 47));

        binding.chartHourlyBreakdown.getDescription().setEnabled(false);

        binding.chartHourlyBreakdown.setWebLineWidth(1f);
        binding.chartHourlyBreakdown.setWebColor(Color.WHITE);
        binding.chartHourlyBreakdown.setWebLineWidthInner(1f);
        binding.chartHourlyBreakdown.setWebColorInner(Color.WHITE);
        binding.chartHourlyBreakdown.setWebAlpha(100);

        binding.chartHourlyBreakdown.animateXY(1400, 1400, Easing.EaseInOutQuad);

        XAxis xAxis = binding.chartHourlyBreakdown.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final String[] mActivities = getResources().getStringArray(R.array.hours_daily);

            @Override
            public String getFormattedValue(float value) {
                if (value/2 < mActivities.length) {
                    if (value%2 == 0) {
                        Log.d("Thenv", "getFormattedValue: " + value);
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


        YAxis yAxis = binding.chartHourlyBreakdown.getYAxis();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setLabelCount(5, true);
        yAxis.setDrawLabels(false);

        binding.chartHourlyBreakdown.getLegend().setEnabled(false);

        setDataForHourlyChart(usageTimePerHour);
    }

    private void setDataForHourlyChart(Float[] usageTimePerHour) {

        ArrayList<RadarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            entries.add(new RadarEntry(usageTimePerHour[i] + 20));
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

        binding.chartHourlyBreakdown.setData(data);
        binding.chartHourlyBreakdown.invalidate();
    }


    private void setupXAxis() {

        MyAxisTitleFormatter xAxisFormatter = new MyAxisTitleFormatter();

        XAxis xl = binding.chartSessionLength.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);
        xl.setTextSize(12f);
        xl.setValueFormatter(xAxisFormatter);
        xl.setTextColor((Color.rgb(155, 154, 155)));
    }

    private void setupYAxis() {
        YAxis yl = binding.chartSessionLength.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setDrawLabels(false);
        yl.setAxisMinimum(0f);

        YAxis yr = binding.chartSessionLength.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setDrawLabels(false);
        yr.setAxisMinimum(0f);
    }

    private void setDataForSessionsLengthChart(HashMap<String, Integer> mapSessionsLength) {

        float barWidth = 9f;
        ArrayList<BarEntry> values = new ArrayList<>();
        float spaceForBar = 10f;
        for (int i = 0; i < 6; i++) {
            float val;
            if (mapSessionsLength.get(String.valueOf(i)) == null) {
                val = 0;
            } else {
                val = (float) mapSessionsLength.get(String.valueOf(i));
            }
            values.add(new BarEntry(i * spaceForBar, val));
        }

        BarDataSet set1;

        if (binding.chartSessionLength.getData() != null &&
                binding.chartSessionLength.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) binding.chartSessionLength.getData().getDataSetByIndex(0);
            set1.setValues(values);
            binding.chartSessionLength.getData().notifyDataChanged();
            binding.chartSessionLength.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "");

            set1.setDrawIcons(false);

            int colorColumn = ContextCompat.getColor(requireContext(), R.color.column_color);
            set1.setColor(colorColumn);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueFormatter(new MyAxisValueFormatter());
            data.setBarWidth(barWidth);
            data.setValueTextColor(Color.rgb(255, 255, 255));
            binding.chartSessionLength.setData(data);
        }
        binding.chartSessionLength.invalidate();
    }

    @Override
    protected void setupListener() {

    }

    @Override
    protected void setupObserver() {

    }

    @Override
    protected void onPermissionGranted() {

    }
}
