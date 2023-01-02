package com.example.apptracking.ui.fragment.search;

import android.os.Bundle;
import android.view.View;
import com.example.apptracking.R;
import com.example.apptracking.databinding.FragmentSearchBinding;
import com.example.apptracking.ui.base.BaseBindingFragment;

public class SearchFragment extends BaseBindingFragment<FragmentSearchBinding, SearchViewModel> {
    @Override
    protected Class<SearchViewModel> getViewModel() {
        return SearchViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void setupListener() {

    }

    @Override
    protected void setupObserver() {

    }

    @Override
    protected void onPermissionGranted() {

    }
}
