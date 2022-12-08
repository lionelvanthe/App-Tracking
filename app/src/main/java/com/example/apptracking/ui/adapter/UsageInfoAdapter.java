package com.example.apptracking.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.ItemMoreLayoutBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.utils.Utils;

import java.util.List;

public class UsageInfoAdapter extends BaseAdapter<App> {
    private long totalUsageTime;
    private Context context;

    public UsageInfoAdapter(Context context, long totalUsageTime, int layoutID, @NonNull ItemClickListener<App> itemClickListener) {
        super(layoutID, itemClickListener);
        this.context = context;
        this.totalUsageTime = totalUsageTime;
    }

    @NonNull
    @Override
    public BaseViewHolder setViewHolder(@NonNull ViewDataBinding binding) {
        return new UsageInfoAdapter.TrendingViewHolder((ItemMoreLayoutBinding) binding);
    }

    class TrendingViewHolder extends BaseViewHolder {

        private ItemMoreLayoutBinding binding;

        public TrendingViewHolder(@NonNull ItemMoreLayoutBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        public void bindData(@NonNull App data) {
            double percentUsageTime = 0;
            if (totalUsageTime != 0) {
                percentUsageTime = (double) (data.getUsageTimeOfDay() * 100) / (double) totalUsageTime;
            }
            binding.tvFeatureUsage.setText("ABC");
            binding.tvTimeUsage.setText(Utils.formatMilliSeconds(data.getUsageTimeOfDay()));
        }

        @Override
        public void clickListener(@NonNull App data, @NonNull ItemClickListener<App> itemClickListener) {

        }
    }
}