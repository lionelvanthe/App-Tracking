package com.example.apptracking.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.palette.graphics.Palette;

import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.ItemAppLayoutBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.utils.Utils;

import java.util.List;
import java.util.Locale;

public class AppAdapter extends BaseAdapter<App> {

    private long totalUsageTime;
    private Context context;

    public AppAdapter(Context context, long totalUsageTime, int layoutID, @NonNull List<App> list, @NonNull ItemClickListener<App> itemClickListener) {
        super(layoutID, list, itemClickListener);
        this.context = context;
        this.totalUsageTime = totalUsageTime;
    }

    @NonNull
    @Override
    public BaseViewHolder setViewHolder(@NonNull ViewDataBinding binding) {
        return new TrendingViewHolder((ItemAppLayoutBinding) binding);
    }

    class TrendingViewHolder extends BaseViewHolder {

        private ItemAppLayoutBinding binding;

        public TrendingViewHolder(@NonNull ItemAppLayoutBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        public void bindData(@NonNull App data) {

            double percentUsageTime = 0;
            if (totalUsageTime != 0) {
                percentUsageTime = (double)(data.getUsageTimeOfDay() * 100) / (double) totalUsageTime;
            }
            Log.d("Thenv", "bindData: " + data.getName());
            binding.tvAppName.setText(data.getName());
            binding.imgIconApp.setBackground(Utils.getPackageIcon(context, data.getPackageName()));
            binding.tvUsageTime.setText(Utils.formatMilliSeconds(data.getUsageTimeOfDay()));
            binding.tvPercentUsageTime.setText(context.getString(R.string.usage_time_percent, String.format(Locale.getDefault(),"%,.1f", percentUsageTime)));

            if (percentUsageTime < 1 && totalUsageTime != 0) {
                binding.progressBar.setProgress(1);
            } else {
                binding.progressBar.setProgress((int) percentUsageTime);
            }
            setColorForProgressbar(binding.progressBar, data.getPackageName());
        }

        @Override
        public void clickListener(@NonNull App data, @NonNull ItemClickListener<App> itemClickListener) {

        }
        private void setColorForProgressbar(ProgressBar progressBar, String packageName) {

            Bitmap bitmap = Utils.drawableToBitmap(Utils.getPackageIcon(context, packageName));
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    if (swatch != null) {
                        progressBar.setProgressTintList(ColorStateList.valueOf(swatch.getRgb()));
                    } else {
                        progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.holo_blue_light)));
                    }
                }
            });
        }
    }
}