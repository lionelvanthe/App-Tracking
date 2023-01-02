package com.example.apptracking.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.palette.graphics.Palette;

import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.databinding.ItemAppLayoutBinding;
import com.example.apptracking.databinding.ItemSpinnerAppLayoutBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppAdapter extends BaseAdapter<AppUsageLimit> {

    private Context context;

    public AppAdapter(Context context, int layoutId, ItemClickListener<AppUsageLimit> itemClickListener) {
        super(layoutId, itemClickListener);
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder setViewHolder(@NonNull ViewDataBinding binding) {
        return new AppAdapter.AppViewHolder((ItemSpinnerAppLayoutBinding) binding);
    }

    class AppViewHolder extends BaseViewHolder {

        private ItemSpinnerAppLayoutBinding binding;

        public AppViewHolder(@NonNull ItemSpinnerAppLayoutBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        public void bindData(@NonNull AppUsageLimit data) {
            binding.imgIconApp.setBackground(Utils.getPackageIcon(context, data.getPackageName()));
            binding.tvAppName.setText(data.getName());
        }

        @Override
        public void clickListener(@NonNull AppUsageLimit data, @NonNull ItemClickListener<AppUsageLimit> itemClickListener) {
            binding.layoutApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClickListener(data);
                }
            });
        }
    }
}
