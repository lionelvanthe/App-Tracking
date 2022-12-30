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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UsageTime {

    UsageStatsManager mUsageStatsManager;
    private Context context;

    private static UsageTime instance;

    private final HashMap<String, List<UsageEvents.Event>> mapEvents;
    private final HashMap<String, Integer> mapNotificationReceives;

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
        mapNotificationReceives = new HashMap<>();
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

    public void getUsageTime(long startTime, long endTime) {

        mapEvents.clear();
        mapNotificationReceives.clear();
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
                if (currentEvent.getEventType() == 12 ) {
                    if (mapNotificationReceives.get(key) == null) {
                        mapNotificationReceives.put(key, 1);
                    } else {
                        mapNotificationReceives.put(key, mapNotificationReceives.get(key) + 1);
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
            int timesOpened = 0;
            HashMap<String, Integer> mapSessionsLength = new HashMap<>();
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
                    timesOpened++;
                    updateSessionsLength(mapSessionsLength, diff);
                }
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
            appHashMap.get(entry.getKey()).setMapSessionsLength(mapSessionsLength);
            appHashMap.get(entry.getKey()).setUsageTimeOfDay(timeInForeground);
            appHashMap.get(entry.getKey()).setTimesOpened(timesOpened);
        }
    }

    private void addValueToHashMap(int key, float value, HashMap<Integer, Float> hashMap) {
        if (hashMap.get(key) == null) {
            hashMap.put(key, value);
        } else {
            hashMap.put(key, hashMap.get(key) + value);
        }
    }

    public void updateSessionsLength(HashMap<String, Integer> mapSessionsLength, long value) {
        if (value > Const.A_HOUR) {
            if (mapSessionsLength.get("5") == null) {
                mapSessionsLength.put("5", 1);
            } else {
                mapSessionsLength.put("5", mapSessionsLength.get("5") + 1);
            }
        } else if (value >= Const.THIRTY_MINUS) {
            if (mapSessionsLength.get("4") == null) {
                mapSessionsLength.put("4", 1);
            } else {
                mapSessionsLength.put("4", mapSessionsLength.get("4") + 1);
            }
        } else if (value >= Const.FIFTEEN_MINUS) {
            if (mapSessionsLength.get("3") == null) {
                mapSessionsLength.put("3", 1);
            } else {
                mapSessionsLength.put("3", mapSessionsLength.get("3") + 1);
            }
        } else if (value >= Const.FIVE_MINUS){
            if (mapSessionsLength.get("2") == null) {
                mapSessionsLength.put("2", 1);
            } else {
                mapSessionsLength.put("2", mapSessionsLength.get("2") + 1);
            }
        } else if (value >= Const.A_MINUS) {
            if (mapSessionsLength.get("1") == null) {
                mapSessionsLength.put("1", 1);
            } else {
                mapSessionsLength.put("1", mapSessionsLength.get("1") + 1);
            }
        } else {
            if (mapSessionsLength.get("0") == null) {
                mapSessionsLength.put("0", 1);
            } else {
                mapSessionsLength.put("0", mapSessionsLength.get("0") + 1);
            }
        }
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

    public long geUsageTimeFollowPackageName(String packageName) {
        App app = mapAppInToDay.get(packageName);
        return app.getUsageTimeOfDay();
    }

    public Float[] getUsageTimePerHourFollowPackageName(String packageName) {
        App app = mapAppInToDay.get(packageName);
        return app.getUsageTimePerHour();
    }

    public int getNotificationReceive(String packageName) {

        if (mapNotificationReceives.get(packageName) != null) {
            return mapNotificationReceives.get(packageName);
        } else {
            return 0;
        }
    }
}