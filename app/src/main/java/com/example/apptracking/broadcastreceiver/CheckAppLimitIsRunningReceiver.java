package com.example.apptracking.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.ui.activity.limit.LimitReachedActivity;
import com.example.apptracking.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class CheckAppLimitIsRunningReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Bundle args = intent.getBundleExtra(Const.EXTRA_APP_USAGE_TIME_LIMIT);
        AppUsageLimit appUsageLimit = (AppUsageLimit) args.getSerializable(Const.APP_USAGE_TIME_LIMIT);

        if (!isAppRunning(context, appUsageLimit.getPackageName())) {
            appUsageLimit.setAlarmCheckAppLimitIsRunning(context, manager);
            Log.d("Thenv", "onReceive: hahahaha");
        } else {
            try {
                Log.d("Thenv", "onReceive: 2");
                Intent ringer = new Intent(context, LimitReachedActivity.class);
                ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ringer.putExtra(Const.EXTRA_APP_USAGE_TIME_LIMIT_TO_ALARM_ACTIVITY, appUsageLimit);
                context.startActivity(ringer);
            } catch (Exception e) {
                Log.d("Thenv", "onReceive: " + e);
            }
        }
    }

    private boolean isAppRunning(Context context, String packageName) {
        List<Integer> eventTypes = getListEventType(context, packageName);

        if (eventTypes.size() == 0) {
            return false;
        } else {
            if (eventTypes.get(eventTypes.size() - 1) == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    private List<Integer> getListEventType(Context context, String packageName) {
        UsageEvents.Event currentEvent;
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<Integer> eventTypes = new ArrayList<>();
        if (mUsageStatsManager != null) {
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(System.currentTimeMillis() - 4000, System.currentTimeMillis());
            while (usageEvents.hasNextEvent()) {
                currentEvent = new UsageEvents.Event();
                usageEvents.getNextEvent(currentEvent);
                if ( currentEvent.getPackageName().equals(packageName)) {
                    if (currentEvent.getEventType() == 1 || currentEvent.getEventType() == 2 || currentEvent.getEventType() == 23) {
                        eventTypes.add(currentEvent.getEventType());
                    }
                }
            }
        }
        return eventTypes;
    }
}
