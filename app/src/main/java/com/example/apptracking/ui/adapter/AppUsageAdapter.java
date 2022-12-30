package com.example.apptracking.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import com.example.apptracking.AppApplication;
import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.ItemAppLayoutBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.utils.Utils;
import java.util.Locale;

public class AppUsageAdapter extends BaseAdapter<App> {

    private long totalUsageTime;
    private Context context;

    public AppUsageAdapter(Context context, int layoutID, @NonNull ItemClickListener<App> itemClickListener) {
        super(layoutID, itemClickListener);
        this.context = context;
    }

    public void setTotalUsageTime(long totalUsageTime) {
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
            binding.tvAppName.setText(data.getName());
            binding.imgIconApp.setBackground(Utils.getPackageIcon(context, data.getPackageName()));
            binding.tvUsageTime.setText(Utils.formatMilliSeconds(data.getUsageTimeOfDay()));
            binding.tvPercentUsageTime.setText(context.getString(R.string.usage_time_percent, String.format(Locale.getDefault(),"%,.1f", percentUsageTime)));

            if (percentUsageTime < 1 && totalUsageTime != 0) {
                binding.progressBar.setProgress(1);
            } else {
                binding.progressBar.setProgress((int) percentUsageTime);
            }

            if (AppApplication.getHashMapDomainColor().get(data.getPackageName()) == null) {
                binding.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.holo_blue_light)));
            } else {
                binding.progressBar.setProgressTintList(ColorStateList.valueOf(AppApplication.getHashMapDomainColor().get(data.getPackageName())));

            }

        }

        @Override
        public void clickListener(@NonNull App data, @NonNull ItemClickListener<App> itemClickListener) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClickListener(data);
                }
            });
        }
    }
}