package com.example.apptracking.data.repository;

import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UsageTimeRepository {
    private UsageTime usageTime;

    public UsageTimeRepository(UsageTime usageTime) {
        this.usageTime = usageTime;
    }

    public Single<List<App>> getUsageTimeOfApps(long startTime, long endTime) {
        return Single.fromCallable(() -> usageTime.getUsageTime(startTime, endTime))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public Single<ArrayList<Float>> getListUsageTimePerHourOfDevice() {
        return Single.fromCallable(() -> usageTime.getListUsageTimePerHourOfDevice())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public long getTotalUsageTime() {
        return usageTime.getTotalUsageTime();
    }
}
