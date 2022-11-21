package com.example.apptracking.ui.fragment.usagelimits;

import android.os.Bundle;
import android.view.View;

import com.example.apptracking.R;
import com.example.apptracking.databinding.FragmentUsageLimitsBinding;
import com.example.apptracking.ui.base.BaseBindingFragment;

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

    }

    @Override
    protected void setupObserver() {

    }

    @Override
    protected void onPermissionGranted() {

    }
}
