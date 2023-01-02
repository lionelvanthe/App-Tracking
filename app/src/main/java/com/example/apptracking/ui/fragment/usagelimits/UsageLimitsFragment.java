package com.example.apptracking.ui.fragment.usagelimits;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apptracking.R;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.databinding.FragmentUsageLimitsBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.adapter.AppUsageLimitAdapter;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.dialog.AlertDialog;
import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;

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

        adapter = new AppUsageLimitAdapter(requireContext(), R.layout.item_app_usage_limit_layout, new ItemClickListener<AppUsageLimit>() {
            @Override
            public void onClickListener(AppUsageLimit model) {
                NavDirections action = UsageLimitsFragmentDirections.actionNavigationUsageLimitsToAddUsageLimitDialog(model);
                Navigation.findNavController(view).navigate(action);

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
    protected void setupListener() {
        binding.fabAddUsageLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && !Hawk.get(Const.INFO_BACKGROUND_PERMISSIONS, false)) {
                    AlertDialog alert = new AlertDialog(requireContext());
                    alert.setTitle(getString(R.string.info_background_permissions_title));
                    alert.setContent(getString(R.string.info_background_permissions_body));
                    alert.setListener((dialog, ok) -> {
                        if (ok) {
                            Hawk.put(Const.INFO_BACKGROUND_PERMISSIONS, true);
                            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                        }
                    });
                    alert.show();
                } else {
                    NavDirections action = UsageLimitsFragmentDirections.actionNavigationUsageLimitsToAddUsageLimitDialog(null);
                    Navigation.findNavController(v).navigate(action);
                }
            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void setupObserver() {

        viewModel.packageNames.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (strings != null) {
                    for (String packageName: strings) {
                        if ( viewModel.getUsageTimePerHourFollowPackageName(packageName) != null) {
                            Hawk.put(packageName, viewModel.getUsageTimePerHourFollowPackageName(packageName));
                        }
                        if (viewModel.geUsageTimeFollowPackageName(packageName) != 0) {
                            viewModel.updateUsageTimeOfDay(viewModel.geUsageTimeFollowPackageName(packageName), packageName);
                        }
                    }
                }
            }
        });

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
