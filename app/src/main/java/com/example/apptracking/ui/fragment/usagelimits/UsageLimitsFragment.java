package com.example.apptracking.ui.fragment.usagelimits;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.apptracking.R;
import com.example.apptracking.databinding.FragmentUsageLimitsBinding;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.dialog.AddUsageLimitDialog;
import com.example.apptracking.ui.dialog.UsageAccessPermissionDialog;

public class UsageLimitsFragment extends BaseBindingFragment<FragmentUsageLimitsBinding, UsageLimitsViewModel> {
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
    }

    @Override
    protected void setupObserver() {

    }

    @Override
    protected void onPermissionGranted() {

    }
}
