package com.example.apptracking.ui.activity.main;



import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.apptracking.R;
import com.example.apptracking.databinding.ActivityMainBinding;
import com.example.apptracking.ui.base.BaseBindingActivity;
import com.example.apptracking.ui.dialog.UsageAccessPermissionDialog;
import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> {

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void setupData() {
        setStatusBarColor();
    }

    protected void setStatusBarColor(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_bg_root));
    }
}