package com.example.apptracking.ui.fragment.more;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.FragmentMoreBinding;
import com.example.apptracking.ui.adapter.AppAdapter;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.dialog.UsageAccessPermissionDialog;
import com.example.apptracking.ui.fragment.home.HomeFragmentDirections;
import com.example.apptracking.ui.fragment.home.HomeViewModel;
import com.example.apptracking.utils.Utils;

import java.util.List;

public class MoreFragment extends BaseBindingFragment<FragmentMoreBinding, HomeViewModel> {
    BaseAdapter<App> adapter;
    UsageAccessPermissionDialog accessPermissionDialog = null;

    @Override
    protected Class<HomeViewModel> getViewModel() {
        return HomeViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void setupListener() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavDirections action = MoreFragmentDirections.actionNavigationMoreToNavigationHome();
                Navigation.findNavController(v).navigate(action);
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
