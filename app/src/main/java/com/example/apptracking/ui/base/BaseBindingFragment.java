package com.example.apptracking.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

abstract public class BaseBindingFragment<B extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragment {

    protected B binding;
    protected VM viewModel;

    abstract protected Class<VM> getViewModel();

    abstract protected int getLayoutId();

    abstract protected void onCreatedView(View view, Bundle savedInstanceState);

    abstract protected void setupListener();

    abstract protected void setupObserver();

    abstract protected void onPermissionGranted();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        }
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this).get(getViewModel());
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onCreatedView(view, savedInstanceState);
        setupListener();
        setupObserver();
    }

}
