package com.example.apptracking.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.apptracking.R;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.databinding.ItemAppUsageLimitLayoutBinding;
import com.example.apptracking.databinding.ItemSpinnerAppLayoutBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;

import java.util.Locale;

public class AppUsageLimitAdapter extends BaseAdapter<AppUsageLimit> {

    private Context context;

    public AppUsageLimitAdapter(Context context, int layoutId, ItemClickListener<AppUsageLimit> itemClickListener) {
        super(layoutId, itemClickListener);
        this.context = context;
    }

    @Override
    protected BaseAdapter<AppUsageLimit>.BaseViewHolder setViewHolder(ViewDataBinding binding) {
        return new AppUsageLimitViewHolder((ItemAppUsageLimitLayoutBinding) binding);
    }

    class AppUsageLimitViewHolder extends BaseViewHolder {

        private ItemAppUsageLimitLayoutBinding binding;

        public AppUsageLimitViewHolder(@NonNull ItemAppUsageLimitLayoutBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        public void bindData(@NonNull AppUsageLimit data) {
            if (data.getName().equals(Const.TOTAL_USAGE)) {
                binding.imgIconApp.setBackgroundResource(R.drawable.ic_launcher_foreground);
            } else {
                binding.imgIconApp.setBackground(Utils.getPackageIcon(context, data.getPackageName()));
            }
            double percentUsageTime = 0;
            percentUsageTime = (double)(data.getUsageTimeOfDay() * 100) / (double) (data.getUsageTimeLimit());
            binding.tvAppName.setText(data.getName());

            binding.tvUsageTimeLimit.setText(Utils.formatMilliSeconds(data.getUsageTimeLimit()));
            binding.tvTodayUsage.setText(context.getString(R.string.today_usage, Utils.formatMilliSeconds(data.getUsageTimeOfDay())));

            if (percentUsageTime >= 100) {
                binding.tvPercent.setText(context.getString(R.string.limit_reached));
            } else {
                binding.tvPercent.setText(context.getString(R.string.usage_time_percent, String.format(Locale.getDefault(),"%,.1f", percentUsageTime)));

            }
        }

        @Override
        public void clickListener(@NonNull AppUsageLimit data, @NonNull ItemClickListener<AppUsageLimit> itemClickListener) {

        }
    }
}
