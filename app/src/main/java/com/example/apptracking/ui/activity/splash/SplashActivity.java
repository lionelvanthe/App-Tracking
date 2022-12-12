package com.example.apptracking.ui.activity.splash;

import static androidx.navigation.Navigation.findNavController;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.example.apptracking.R;
import com.example.apptracking.databinding.ActivityAlarmBinding;
import com.example.apptracking.databinding.ActivityMainBinding;
import com.example.apptracking.ui.activity.main.MainActivity;
import com.example.apptracking.ui.activity.main.MainViewModel;
import com.example.apptracking.ui.base.BaseBindingActivity;

public class SplashActivity extends BaseBindingActivity<ActivityAlarmBinding, MainViewModel> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    public Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public void setupView(Bundle savedInstanceState) {
        setStatusBarColor();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    @Override
    public void setupData() {
    }

    protected void setStatusBarColor(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_bg_root));
    }
}