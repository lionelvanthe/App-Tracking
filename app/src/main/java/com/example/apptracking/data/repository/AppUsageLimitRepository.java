package com.example.apptracking.data.repository;

import androidx.lifecycle.LiveData;

import com.example.apptracking.data.local.dao.AppUsageLimitDAO;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.model.AppUsageLimit;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppUsageLimitRepository {

    private AppUsageLimitDAO appUsageLimitDAO;

    public AppUsageLimitRepository(AppUsageLimitDAO appUsageLimitDAO) {
        this.appUsageLimitDAO = appUsageLimitDAO;
    }

    public void createAppUsageLimit(AppUsageLimit appUsageLimit) {
         appUsageLimitDAO.createAppUsageLimit(appUsageLimit);
    }

    public LiveData<List<AppUsageLimit>> getAppUsageLimits() {
        return appUsageLimitDAO.getAppUsageLimits();
    }

    public void deleteAppUsageLimit(AppUsageLimit alarm) {
        appUsageLimitDAO.deleteAppUsageLimit(alarm);
    }

    public Single<List<String>> getPackageNames() {
        return Single.fromCallable(() ->appUsageLimitDAO.getPackageNames())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public Completable updateUsageTimeOfDay(long usageTime, String packageName) {

        return Completable.fromRunnable(() -> appUsageLimitDAO.updateUsageTimeOfDay(usageTime, packageName))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());

    }

}
