package com.example.apptracking.ui.activity;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.example.apptracking.R;
import com.example.apptracking.databinding.ActivityAlarmBinding;
import com.example.apptracking.databinding.ActivityMainBinding;
import com.example.apptracking.ui.activity.main.MainViewModel;
import com.example.apptracking.ui.base.BaseBindingActivity;

public class AlarmActivity extends BaseBindingActivity<ActivityAlarmBinding, MainViewModel> {

    public static final String EXTRA_ALARM = "james.alarmio.AlarmActivity.EXTRA_ALARM";

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
        Log.d("Thenv", "setupView: hahahaha");
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
