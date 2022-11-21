package com.example.apptracking.ui.fragment.home;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.lifecycle.Observer;
import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.DialogUsageAccessPermissionBinding;
import com.example.apptracking.databinding.FragmentHomeBinding;
import com.example.apptracking.ui.adapter.AppAdapter;
import com.example.apptracking.ui.base.BaseAdapter;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.ui.dialog.UsageAccessPermissionDialog;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding, HomeViewModel> {

    BaseAdapter<App> adapter;
    UsageAccessPermissionDialog accessPermissionDialog = null;

    @Override
    protected Class<HomeViewModel> getViewModel() {
        return HomeViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        if (isPermissionGranted()) {
            if (Hawk.get(Const.END_TIME_HOUR, -1) == -1) {
                onPermissionGranted();
                Hawk.put(Const.END_TIME_HOUR, 1);
            }
        } else {
            binding.layoutRoot.setVisibility(View.GONE);
            accessPermissionDialog = new UsageAccessPermissionDialog();
            accessPermissionDialog.setCancelable(false);
            accessPermissionDialog.show(requireActivity().getSupportFragmentManager(), "usage_access_permission");
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (accessPermissionDialog != null && isPermissionGranted()) {
            accessPermissionDialog.dismiss();
            binding.layoutRoot.setVisibility(View.VISIBLE);
            if (Hawk.get(Const.END_TIME_HOUR, -1) == -1) {
                onPermissionGranted();
                Hawk.put(Const.END_TIME_HOUR, 1);
            }
        }
    }

    @Override
    protected void setupLister() {

    }

    @Override
    protected void setupObserver() {
        viewModel.apps.observe(this, new Observer<List<App>>() {
            @Override
            public void onChanged(List<App> apps) {
                if (adapter == null) {
                    adapter = new AppAdapter(requireContext(),
                            viewModel.getTotalUsageTime(),
                            R.layout.item_app_layout, apps,
                            model -> {

                            });
                }
                binding.recyclerViewApp.setAdapter(adapter);
                viewModel.getListUsageTimePerHourOfDevice();
                binding.tvTotalUsageTimeContent.setText(Utils.formatMilliSeconds(viewModel.getTotalUsageTime()));
            }
        });

        viewModel.listUsageTimePerHourOfDevice.observe(this, new Observer<List<Float>>() {
            @Override
            public void onChanged(List<Float> floats) {
                binding.barView.setDataList(floats,60);
            }
        });

        viewModel.isGetDataComplete.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progress.setVisibility(View.GONE);
                    binding.recyclerViewApp.setVisibility(View.VISIBLE);
                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.recyclerViewApp.setVisibility(View.GONE);
                }
            }
        });
    }


    private boolean isPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) requireActivity().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), requireContext().getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    protected void onPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long timeNow = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeNow);
            cal.set(Calendar.DAY_OF_MONTH, 18);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(timeNow);
            cal2.set(Calendar.DAY_OF_MONTH, 18);
            cal2.set(Calendar.HOUR_OF_DAY, 23);
            cal2.set(Calendar.MINUTE, 59);
            cal2.set(Calendar.SECOND, 59);
            cal2.set(Calendar.MILLISECOND, 99);

            viewModel.getListApp(cal.getTimeInMillis(), cal2.getTimeInMillis());
        }
    }
}
