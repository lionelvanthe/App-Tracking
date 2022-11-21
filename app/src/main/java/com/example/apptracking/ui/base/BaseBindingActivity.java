package com.example.apptracking.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseBindingActivity<B extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity{
    protected B binding;
    protected VM viewModel;

    abstract public int getLayoutId();
    abstract public Class<VM> getViewModel();
    abstract public void setupView(Bundle savedInstanceState);
    abstract public void setupData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, getLayoutId());
        viewModel = new ViewModelProvider(this).get(getViewModel());
        setupView(savedInstanceState);
        setupData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}