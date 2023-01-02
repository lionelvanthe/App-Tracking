package com.example.apptracking.ui.fragment.more;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.FragmentMoreBinding;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.custom.HourlyChart;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MoreFragment extends BaseBindingFragment<FragmentMoreBinding, MoreViewModel> {

    @Override
    protected Class<MoreViewModel> getViewModel() {
        return MoreViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        binding.tvUsageContent.setText(Utils.formatMilliSeconds(viewModel.getTotalUsageTime()));
        binding.tvDeviceUnlocksContent.setText(String.valueOf(viewModel.getDeviceUnlocks()));
        binding.tvNotificationContent.setText(String.valueOf(viewModel.getNotificationReceives()));
        binding.tvAverageContent.setText(Utils.formatMilliSeconds(viewModel.getTotalUsageTime()/24));

        pieChart();

    }

    @Override
    protected void setupListener() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }


    @Override
    protected void setupObserver() {
        viewModel.deviceUnlocksPerHour.observe(getViewLifecycleOwner(), new Observer<List<Float>>() {
            @Override
            public void onChanged(List<Float> floats) {
                CopyOnWriteArrayList<Float> arrayList = new CopyOnWriteArrayList<>(floats);
                binding.barView.setDataList(arrayList, (int) viewModel.getMaxDeviceUnlocks(), false);
            }
        });

        viewModel.listUsageTimePerHourOfDevice.observe(getViewLifecycleOwner(), new Observer<List<Float>>() {
            @Override
            public void onChanged(List<Float> floats) {
                CopyOnWriteArrayList<Float> arrayList = new CopyOnWriteArrayList<>(floats);
                HourlyChart hourlyChart = new HourlyChart(binding.chartHourlyBreakdown, arrayList, requireContext());
                hourlyChart.setDataForHourlyChart();
            }
        });

        viewModel.apps.observe(getViewLifecycleOwner(), new Observer<List<App>>() {
            @Override
            public void onChanged(List<App> apps) {
                setData(apps);
            }
        });
    }

    private void pieChart() {
        binding.chartSessionLength.setDrawRoundedSlices(true);
        binding.chartSessionLength.setUsePercentValues(true);
        binding.chartSessionLength.getDescription().setEnabled(false);
        binding.chartSessionLength.setExtraOffsets(22, 22, 22, 22);

        binding.chartSessionLength.setDragDecelerationFrictionCoef(0.5f);

        binding.chartSessionLength.setCenterText(generateCenterSpannableText());

        binding.chartSessionLength.setDrawHoleEnabled(true);
        binding.chartSessionLength.setHoleColor(getResources().getColor(R.color.color_bg_root));

        binding.chartSessionLength.setTransparentCircleColor(Color.WHITE);
        binding.chartSessionLength.setTransparentCircleAlpha(110);

        binding.chartSessionLength.setHoleRadius(90f);
        binding.chartSessionLength.setTransparentCircleRadius(61f);

        binding.chartSessionLength.setDrawCenterText(true);

        binding.chartSessionLength.setRotationAngle(70);
        // enable rotation of the chart by touch
        binding.chartSessionLength.setRotationEnabled(true);
        binding.chartSessionLength.setHighlightPerTapEnabled(true);
        binding.chartSessionLength.setDrawHoleEnabled(true);

        binding.chartSessionLength.animateY(1400, Easing.EaseInOutQuad);

        binding.chartSessionLength.getLegend().setEnabled(false);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString(Utils.formatMilliSeconds(viewModel.getTotalUsageTime()));
        s.setSpan(new RelativeSizeSpan(1.5f), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        return s;
    }

    private void setData(List<App> apps) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        long usageTimeOfOtherApp = viewModel.getTotalUsageTime();

        for (App app : apps) {
            float usageTime = (float) app.getUsageTimeOfDay();
            if (usageTime > Const.FIFTEEN_MINUS) {
                usageTimeOfOtherApp -= usageTime;
                entries.add(new PieEntry(usageTime, resizeDrawable(Utils.getPackageIcon(requireContext(), app.getPackageName()))));
            }
        }

        if (usageTimeOfOtherApp != 0) {
            entries.add(new PieEntry((float) usageTimeOfOtherApp, resizeDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_foreground))));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setFormSize(100f);

        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(2f);
        dataSet.setIconsOffset(new MPPointF(0, 27));
        dataSet.setSelectionShift(3f);

        setColorData(dataSet);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        binding.chartSessionLength.setData(data);

        binding.chartSessionLength.invalidate();
    }

    private Drawable resizeDrawable(Drawable drawable) {
        Bitmap bitmap = Utils.drawableToBitmap(drawable);
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
    }

    private void setColorData(PieDataSet dataSet) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(51, 181, 229));
        colors.add(Color.rgb(252,68,68));
        colors.add(Color.rgb(155,205,0));
        colors.add(Color.rgb(241,196,15));
        colors.add(Color.rgb(169,101,202));
        colors.add(Color.rgb(255, 102, 0));
        dataSet.setColors(colors);
    }

    @Override
    protected void onPermissionGranted() {

    }
}
