package com.example.apptracking.data.local;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import com.example.apptracking.AppApplication;
import com.example.apptracking.data.model.App;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.orhanobut.hawk.Hawk;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UsageTime {

    UsageStatsManager mUsageStatsManager;
    private Context context;

    private static UsageTime instance;

    private final HashMap<String, List<UsageEvents.Event>> mapEvents;

    private final HashMap<String, App> mapAppInToDay;
    private HashMap<String, App> mapApp;

    private final HashMap<Integer, Float> hashMapUsageTimePerHourOfDeviceInToday;
    private final HashMap<Integer, Float> hashMapUsageTimePerHourOfDevice;

    private long totalUsageTimeInToday = 0;
    private long totalUsageTime = 0;

    public HashMap<String, App> getMapAppInToday() {
        return mapAppInToDay;
    }

    public UsageTime(Context context) {
        this.context = context;
        mapAppInToDay = new HashMap<>();
        mapApp = new HashMap<>();
        mapEvents = new HashMap<>();
        hashMapUsageTimePerHourOfDevice = new HashMap<>();
        hashMapUsageTimePerHourOfDeviceInToday = new HashMap<>();
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public static UsageTime getInstance(Context context)
    {
        if (instance== null) {
            synchronized(AppApplication.class) {
                if (instance == null)
                    instance = new UsageTime(context);
            }
        }
        return instance;
    }

    public List<App> getApps() {
        if (Hawk.get(Const.IS_TODAY)) {
            return new ArrayList<>(mapAppInToDay.values());

        } else {
            return new ArrayList<>(mapApp.values());
        }
    }

    public long getTotalUsageTime() {
        if (Hawk.get(Const.IS_TODAY)) {
            return totalUsageTimeInToday;

        } else {
            return totalUsageTime;
        }
    }

    public HashMap<Integer, Float> getHashMapUsageTimePerHourOfDevice() {
        if (Hawk.get(Const.IS_TODAY)) {
            return  hashMapUsageTimePerHourOfDeviceInToday;
        } else {
            return hashMapUsageTimePerHourOfDevice;
        }
    }

    public void getUsageTime(long startTime, long endTime) {

        mapEvents.clear();
        if (Hawk.get(Const.IS_TODAY)) {
            mapAppInToDay.clear();
            hashMapUsageTimePerHourOfDeviceInToday.clear();
            totalUsageTimeInToday = 0;
            initHashMap(startTime, endTime, mapAppInToDay);
            calculateUsageTime(endTime, mapAppInToDay, true, hashMapUsageTimePerHourOfDeviceInToday);

        } else {
            mapApp.clear();
            hashMapUsageTimePerHourOfDevice.clear();
            totalUsageTime = 0;
            initHashMap(startTime, endTime, mapApp);
            calculateUsageTime(endTime, mapApp, false, hashMapUsageTimePerHourOfDevice);
        }
    }

    public long geUsageTimeFollowPackageName(String packageName) {
        App app = mapAppInToDay.get(packageName);
        return app.getUsageTimeOfDay();
    }

    public Float[] getUsageTimePerHourFollowPackageName(String packageName) {
        App app = mapAppInToDay.get(packageName);
        return app.getUsageTimePerHour();
    }

    private void initHashMap(long startTime, long endTime, HashMap<String, App> appHashMap) {
        UsageEvents.Event currentEvent;
        if (mUsageStatsManager != null) {
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, endTime);
            while (usageEvents.hasNextEvent()) {
                currentEvent = new UsageEvents.Event();
                usageEvents.getNextEvent(currentEvent);
                String key = currentEvent.getPackageName();
                if (AppApplication.getHashMapAppOpenableAndInstalled().get(key) != null) {
                    if (currentEvent.getEventType() == 1 || currentEvent.getEventType() == 2 || currentEvent.getEventType() == 23) {
                        if (appHashMap.get(key) == null) {
                            appHashMap.put(key, new App(Utils.parsePackageName(context.getPackageManager(), key), key));
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

    private void calculateUsageTime(long endTime, HashMap<String, App> appHashMap, boolean isToday, HashMap<Integer, Float> timeHashMap) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2= Calendar.getInstance();

        for (Map.Entry<String, List<UsageEvents.Event>> entry : mapEvents.entrySet()) {
            int totalEvents = entry.getValue().size();
            long timeInForeground = 0;
            Utils.getDomainColor(context, entry.getKey());
            for (int i = 0; i < totalEvents - 2; i++) {

                UsageEvents.Event E0 = entry.getValue().get(i);
                UsageEvents.Event E1 = entry.getValue().get(i + 1);
                UsageEvents.Event E2 = entry.getValue().get(i + 2);

                calendar.setTimeInMillis(E0.getTimeStamp());
                if (E1.getEventType() == 2) {
                    calendar2.setTimeInMillis(E1.getTimeStamp());
                } else {
                    calendar2.setTimeInMillis(E2.getTimeStamp());
                }
                if (E0.getEventType() == 1 && E1.getEventType() != 1) {
                    long diff;

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int hour2 = calendar2.get(Calendar.HOUR_OF_DAY);

                    if (E2.getEventType() == 1) {
                        diff = E1.getTimeStamp() - E0.getTimeStamp();
                        appHashMap.get(entry.getKey()).addUsageTimePerHour(hour, (float) diff / (float) Const.A_MINUS);
                        addValueToHashMap(hour, (float) diff / (float) Const.A_MINUS, timeHashMap);
                    } else {
                        diff = E2.getTimeStamp() - E0.getTimeStamp();

                        if (hour != hour2) {
                            appHashMap.get(entry.getKey()).addUsageTimePerHour(hour, (60 - calendar.get(Calendar.MINUTE)));
                            appHashMap.get(entry.getKey()).addUsageTimePerHour(hour2, calendar2.get(Calendar.MINUTE) );

                            addValueToHashMap(hour, (float) (60 - calendar.get(Calendar.MINUTE)), timeHashMap);
                            addValueToHashMap(hour2, (float) calendar2.get(Calendar.MINUTE), timeHashMap);
                        } else {
                            appHashMap.get(entry.getKey()).addUsageTimePerHour(hour, (float) diff / (float) Const.A_MINUS);
                            addValueToHashMap(hour, (float) diff / (float) Const.A_MINUS, timeHashMap);
                        }
                    }
                    timeInForeground += diff;
                }
            }

            if (entry.getKey().equals("com.facebook.katana")) {
//                Log.d("Thenv", "calculateUsageTime: " + entry.getValue().get(totalEvents - 1).getEventType() + " , " + entry.getValue().get(totalEvents - 1).getTimeStamp());
            }

            if (entry.getValue().get(totalEvents - 1).getEventType() == 1) {
                long diff = endTime - entry.getValue().get(totalEvents - 1).getTimeStamp();
                timeInForeground += diff;
            }
            if (isToday) {
                totalUsageTimeInToday += timeInForeground;
            } else {
                totalUsageTime += timeInForeground;
            }
            appHashMap.get(entry.getKey()).setUsageTimeOfDay(timeInForeground);
        }
    }

    private void addValueToHashMap(int key, float value, HashMap<Integer, Float> hashMap) {
        if (hashMap.get(key) == null) {
            hashMap.put(key, value);
        } else {
            hashMap.put(key, hashMap.get(key) + value);
        }
    }
}