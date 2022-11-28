package com.example.apptracking.ui.fragment.more;

import android.os.Bundle;
import android.view.View;

import com.example.apptracking.R;
import com.example.apptracking.databinding.FragmentMoreBinding;
import com.example.apptracking.ui.base.BaseBindingFragment;

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
