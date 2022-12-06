package com.example.apptracking.ui.fragment.usagelimits;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptracking.R;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.databinding.FragmentUsageLimitsBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.adapter.AppUsageLimitAdapter;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.dialog.AddUsageLimitDialog;
import com.example.apptracking.ui.dialog.UsageAccessPermissionDialog;

import java.util.List;

public class UsageLimitsFragment extends BaseBindingFragment<FragmentUsageLimitsBinding, UsageLimitsViewModel> {

    private AppUsageLimitAdapter adapter;

    @Override
    protected Class<UsageLimitsViewModel> getViewModel() {
        return UsageLimitsViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_usage_limits;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        viewModel.getUsageTimeToday();
        adapter = new AppUsageLimitAdapter(requireContext(), R.layout.item_app_usage_limit_layout, new ItemClickListener<AppUsageLimit>() {
            @Override
            public void onClickListener(AppUsageLimit model) {

            }
        });

        binding.recyclerVieAppUsageLimit.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.fabAddUsageLimit.collapse(true);
                } else{
                   binding.fabAddUsageLimit.expand(true);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void setupLister() {
        binding.fabAddUsageLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUsageLimitDialog addUsageLimitDialog = new AddUsageLimitDialog();
                addUsageLimitDialog.show(requireActivity().getSupportFragmentManager(), "usage_limit_permission");
            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteAllAppUsageLimit();
            }
        });
    }

    @Override
    protected void setupObserver() {
        viewModel.appUsageLimitsInDatabase.observe(getViewLifecycleOwner(), new Observer<List<AppUsageLimit>>() {
            @Override
            public void onChanged(List<AppUsageLimit> appUsageLimits) {
                if (appUsageLimits != null) {
                    adapter.setListData(appUsageLimits);
                    binding.recyclerVieAppUsageLimit.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    protected void onPermissionGranted() {

    }
}
