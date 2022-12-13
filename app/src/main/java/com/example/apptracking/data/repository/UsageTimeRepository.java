package com.example.apptracking.data.repository;

import android.app.Application;
import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.apptracking.AppApplication;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UsageTimeRepository {
    private UsageTime usageTime;


    public UsageTimeRepository(UsageTime usageTime) {
        this.usageTime = usageTime;
    }

    public Completable getUsageTime(long startTime, long endTime, boolean isToday) {
        return Completable.fromRunnable(() -> usageTime.getUsageTime(startTime, endTime, isToday))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public Single<List<App>> getApps() {
        return Single.fromCallable(() -> usageTime.getApps())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public Single<ArrayList<Float>> getListUsageTimePerHourOfDevice() {
        return Single.fromCallable(() -> convertHashMapUsageTimePerHourToList(usageTime.getHashMapUsageTimePerHourOfDevice()))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    private ArrayList<Float> convertHashMapUsageTimePerHourToList(HashMap<Integer, Float> hashMap) {
        ArrayList<Float> listUsageTimePerHour = new ArrayList<>();
        for (int i = 0 ; i < 24 ; i ++) {
            if (hashMap.get(i) == null) {
                listUsageTimePerHour.add(0F);
            } else {
                listUsageTimePerHour.add(hashMap.get(i));
            }
        }
        return listUsageTimePerHour;
    }

    public long getTotalUsageTime() {
        return usageTime.getTotalUsageTime();
    }

    public Single<ArrayList<AppUsageLimit>> getAppsInstalled(Application application) {
        return Single.fromCallable(() ->getApps(application))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    private ArrayList<AppUsageLimit> getApps(Application application) {

        ArrayList<AppUsageLimit> appUsageLimits = new ArrayList<>();

        final PackageManager pm = application.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        HashMap<String, App> mapApp = getMapApp();
        for (ApplicationInfo packageInfo : packages) {
            if (!Utils.isSystemApp(application.getPackageManager(), packageInfo.packageName)) {
                AppUsageLimit appUsageLimit;
                if (mapApp.get(packageInfo.packageName) == null) {
                    appUsageLimit = new AppUsageLimit(Utils.parsePackageName(application.getPackageManager(), packageInfo.packageName), packageInfo.packageName);
                    appUsageLimit.setUsageTimeOfDay(0);
                } else {
                    App app =  mapApp.get(packageInfo.packageName);
                    appUsageLimit = new AppUsageLimit(app.getName(), packageInfo.packageName);
                    appUsageLimit.setUsageTimeOfDay(app.getUsageTimeOfDay());
                }
                appUsageLimits.add(appUsageLimit);
            }
        }
        return appUsageLimits;
    }

    public HashMap<String, App> getMapApp() {
        return usageTime.getMapApp();
    }
}
