package com.example.apptracking.ui.activity.main;

import static androidx.navigation.Navigation.findNavController;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;
import com.example.apptracking.R;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.databinding.ActivityMainBinding;
import com.example.apptracking.ui.base.BaseBindingActivity;
import com.example.apptracking.ui.dialog.UsageAccessPermissionDialog;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.orhanobut.hawk.Hawk;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Calendar;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> {

    UsageAccessPermissionDialog accessPermissionDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public void setupView(Bundle savedInstanceState) {
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (!isPermissionGranted()) {
            binding.getRoot().setVisibility(View.GONE);
            accessPermissionDialog = new UsageAccessPermissionDialog();
            accessPermissionDialog.setCancelable(false);
            accessPermissionDialog.show(getSupportFragmentManager(), "usage_access_permission");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Thenv", "onStart: ");
        if (Hawk.get(Const.IS_TODAY)) {
            Hawk.put(Const.IS_LOAD_DATA, true);

        } else {
            Hawk.put(Const.IS_LOAD_DATA, false);

        }
        if (isPermissionGranted()) {
            if (accessPermissionDialog != null) {
                accessPermissionDialog.dismiss();
            }
            binding.getRoot().setVisibility(View.VISIBLE);
            getUsageTimeOfApps();
        }
    }

    private boolean isPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void getUsageTimeOfApps() {
        long endTime = System.currentTimeMillis();

        viewModel.getUsageTime(Utils.getStartTimeOfToday(), endTime);

    }

    @Override
    public void setupData() {
    }

}