package com.example.apptracking.data.model;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.apptracking.broadcastreceiver.AlarmReceiver;
import com.example.apptracking.broadcastreceiver.CheckAppLimitIsRunningReceiver;
import com.example.apptracking.ui.activity.main.MainActivity;
import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "appUsageLimit")
public class AppUsageLimit extends App implements Serializable {

    protected long usageTimeOfDay;
    private long usageTimeLimit = 0;
    private int warningType;
    private String textDisplayed;

    public AppUsageLimit(String name, String packageName) {
        super(name, packageName);
    }

    public long getUsageTimeLimit() {
        return usageTimeLimit;
    }

    public void setUsageTimeLimit(long usageTimeLimit) {
        this.usageTimeLimit = usageTimeLimit;

    }

    public String getTextDisplayed() {
        return textDisplayed;
    }

    public void setTextDisplayed(String textDisplayed) {
        this.textDisplayed = textDisplayed;
    }

    public int getWarningType() {
        return warningType;
    }

    public void setWarningType(int warningType) {
        this.warningType = warningType;
    }

    public long getUsageTimeOfDay() {
        return usageTimeOfDay;
    }

    public void setUsageTimeOfDay(long usageTimeOfDay) {
        this.usageTimeOfDay = usageTimeOfDay;
    }

    public void setAlarm(Context context, AlarmManager manager) {

        manager.set(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + this.usageTimeLimit - this.usageTimeOfDay,
                getIntent(context, AlarmReceiver.class));
    }

    public void cancel(Context context, AlarmManager manager, Class<?> receiver) {
        manager.cancel(getIntent(context, receiver));
    }

    public void setAlarmCheckAppLimitIsRunning(Context context, AlarmManager manager) {

        long currentTime  = System.currentTimeMillis();
        manager.set(
                AlarmManager.RTC_WAKEUP,
                currentTime + 3000,
                getIntent(context, CheckAppLimitIsRunningReceiver.class)
        );
    }

    private PendingIntent getIntent(Context context, Class<?> receiver) {
        Intent intent = new Intent(context, receiver);
        Bundle args = new Bundle();
        args.putSerializable(Const.APP_USAGE_TIME_LIMIT, this);
        intent.putExtra(Const.EXTRA_APP_USAGE_TIME_LIMIT,args);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context, this.name.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getBroadcast(context, this.name.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
    }

}
