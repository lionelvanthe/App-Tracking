package com.example.apptracking.broadcastreceiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.ui.activity.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_ALARM_ID = "james.alarmio.EXTRA_ALARM_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AppUsageLimit appUsageLimit = (AppUsageLimit) intent.getSerializableExtra(EXTRA_ALARM_ID);

        Log.d("Thenv", "onReceive: hihihihii");

        try {
            Intent ringer = new Intent(context, AlarmActivity.class);
            ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ringer.putExtra(AlarmActivity.EXTRA_ALARM, appUsageLimit);
            context.startActivity(ringer);
        } catch (Exception e) {
            Log.d("Thenv", "onReceive: " + e);
        }
    }
}