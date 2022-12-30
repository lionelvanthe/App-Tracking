package com.example.apptracking.ui.activity.limit;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import androidx.core.content.ContextCompat;
import com.example.apptracking.R;
import com.example.apptracking.broadcastreceiver.AlarmReceiver;
import com.example.apptracking.broadcastreceiver.CheckAppLimitIsRunningReceiver;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.databinding.ActivityLimitReachedBinding;
import com.example.apptracking.ui.activity.main.MainViewModel;
import com.example.apptracking.ui.base.BaseBindingActivity;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.orhanobut.hawk.Hawk;

import java.util.concurrent.CopyOnWriteArrayList;

public class LimitReachedActivity extends BaseBindingActivity<ActivityLimitReachedBinding, MainViewModel> {

    private AppUsageLimit appUsageLimit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_limit_reached;
    }

    @Override
    public Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public void setupView(Bundle savedInstanceState) {
        setUpListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUsageLimit = (AppUsageLimit) getIntent().getSerializableExtra(Const.EXTRA_APP_USAGE_TIME_LIMIT_TO_ALARM_ACTIVITY);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        appUsageLimit.setAlarmCheckAppLimitIsRunning(LimitReachedActivity.this, alarms);

        binding.imgIconApp.setBackground(Utils.getPackageIcon(this, appUsageLimit.getPackageName()));
        binding.tvAppName.setText(appUsageLimit.getName());
        binding.tvUsageLimitContent.setText(Utils.formatMilliSeconds(appUsageLimit.getUsageTimeLimit()));
        binding.tvTodayUsageContent.setText(Utils.formatMilliSeconds(appUsageLimit.getUsageTimeLimit() + 5000));
        if (appUsageLimit.getTextDisplayed() != null || appUsageLimit.getTextDisplayed().equals("")) {
            binding.tvNote.setVisibility(View.GONE);
        } else {
            binding.tvNote.setText(appUsageLimit.getTextDisplayed());
        }
        CopyOnWriteArrayList<Float> arrayList = new CopyOnWriteArrayList<>(Hawk.get(appUsageLimit.getPackageName(), new Float[]{}));
        binding.barView.setDataList(arrayList, 60);

    }

    @Override
    public void setupData() {
        setStatusBarColor();
    }

    private void setUpListener() {
        binding.btnDismissBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                appUsageLimit.cancel(LimitReachedActivity.this, alarms, AlarmReceiver.class);
                appUsageLimit.cancel(LimitReachedActivity.this, alarms, CheckAppLimitIsRunningReceiver.class);
            }
        });
    }

    protected void setStatusBarColor(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_bg_root));
    }
}
