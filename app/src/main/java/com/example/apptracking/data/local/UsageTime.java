package com.example.apptracking.data.local;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.palette.graphics.Palette;

import com.example.apptracking.data.model.App;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageTime {
    
    UsageStatsManager mUsageStatsManager;
    private Context context;

    private final HashMap<String, App> mapApp;
    private final HashMap<String, List<UsageEvents.Event>> mapEvents;
    private final ArrayList<Float> listUsageTimePerHourOfDevice;
    private long totalUsageTime = 0;

    public UsageTime(Context context) {
        this.context = context;
        mapApp = new HashMap<>();
        mapEvents = new HashMap<>();
        listUsageTimePerHourOfDevice = new ArrayList<>();
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public ArrayList<Float> getListUsageTimePerHourOfDevice() {
        return listUsageTimePerHourOfDevice;
    }

    public long getTotalUsageTime() {
        return totalUsageTime;
    }

    public List<App> getUsageTime(long startTime, long endTime) {
        mapApp.clear();
        mapEvents.clear();
        totalUsageTime = 0;
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(endTime);
        int i = 0;
        while (i <= calEnd.get(Calendar.HOUR_OF_DAY)) {
            initHashMap(startTime, startTime + Const.AN_HOUR - 1);
            calculateUsageTime(startTime, startTime + Const.AN_HOUR - 1);
            startTime += Const.AN_HOUR;
            mapEvents.clear();
            i++;
        }
        ArrayList<App> apps = new ArrayList<>();
        for (App app : mapApp.values()) {
            if (app.getUsageTimeOfDay() > 1000) {
                apps.add(app);
                totalUsageTime += app.getUsageTimeOfDay();
            }
        }
        return apps;
}

    private void initHashMap(long startTime, long endTime) {
        UsageEvents.Event currentEvent;
        if (mUsageStatsManager != null) {
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, endTime);
            if (usageEvents.hasNextEvent()) {
                while (usageEvents.hasNextEvent()) {
                    currentEvent = new UsageEvents.Event();
                    usageEvents.getNextEvent(currentEvent);
                    if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED ||
                            currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {
                        String key = currentEvent.getPackageName();
                        if (Utils.isInstalled(context.getPackageManager(), key)) {
                            if (mapApp.get(key) == null) {
                                mapApp.put(key, new App(Utils.parsePackageName(context.getPackageManager(), key), key));
                            }
                            if (mapEvents.get(key) == null) {
                                mapEvents.put(key, new ArrayList<>());
                            }
                            mapEvents.get(key).add(currentEvent);
                        }
                    }
                }
            }
        }
    }

    private void calculateUsageTime(long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        long usageTimePerHourOfDevice = 0;
        for (Map.Entry<String, List<UsageEvents.Event>> entry : mapEvents.entrySet()) {
            int totalEvents = entry.getValue().size();
            long timeInForeground = 0;
            if (totalEvents > 1) {
                for (int i = 0; i < totalEvents - 1; i++) {
                    UsageEvents.Event E0 = entry.getValue().get(i);
                    UsageEvents.Event E1 = entry.getValue().get(i + 1);
//                    if (E1.getEventType() == 1 || E0.getEventType() == 1) {
//                        Objects.requireNonNull(map.get(E1.getPackageName())).launchCount++;
//                    }
                    if (E0.getEventType() == 1 && E1.getEventType() == 2) {
                        long diff = E1.getTimeStamp() - E0.getTimeStamp();
                        timeInForeground += diff;
                    }
                }
            }
            // If First event type is ACTIVITY_PAUSED then added the difference of start_time and Event occuring time because the application is already running.
            if (entry.getValue().get(0).getEventType() == 2) {
                long diff = entry.getValue().get(0).getTimeStamp() - startTime;
                timeInForeground += diff;
            }
            // If Last event type is ACTIVITY_RESUMED then added the difference of end_time and Event occuring time because the application is still running .
            if (entry.getValue().get(totalEvents - 1).getEventType() == 1) {
                long diff = endTime - entry.getValue().get(totalEvents - 1).getTimeStamp();
                timeInForeground += diff;
            }
            if (mapApp.get(entry.getKey()) != null) {
                mapApp.get(entry.getKey()).addUsageTimePerHour(calendar.get(Calendar.HOUR_OF_DAY), timeInForeground);
                mapApp.get(entry.getKey()).plusUsageTime(timeInForeground);
            }
            usageTimePerHourOfDevice+= timeInForeground;
        }
        listUsageTimePerHourOfDevice.add((float) usageTimePerHourOfDevice / (float) Const.A_MINUS);
    }
}
