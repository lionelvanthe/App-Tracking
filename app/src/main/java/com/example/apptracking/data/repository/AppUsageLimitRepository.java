package com.example.apptracking.data.repository;

import androidx.lifecycle.LiveData;

import com.example.apptracking.data.local.dao.AppUsageLimitDAO;
import com.example.apptracking.data.model.AppUsageLimit;

import java.util.List;

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

    public void changeTimeLimit(long time, String name) {
        appUsageLimitDAO.changeTimeLimit(time, name);
    }

    public void deleteAppUsageLimit(AppUsageLimit alarm) {
        appUsageLimitDAO.deleteAppUsageLimit(alarm);
    }

    public void deleteAllAppUsageLimit() {
        appUsageLimitDAO.deleteAllAppUsageLimit();
    }

}
