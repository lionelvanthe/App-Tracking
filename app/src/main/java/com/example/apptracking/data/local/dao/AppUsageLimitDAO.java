package com.example.apptracking.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.apptracking.data.model.AppUsageLimit;

import java.util.List;

@Dao
public interface AppUsageLimitDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createAppUsageLimit(AppUsageLimit appUsageLimit);

    @Query("SELECT * FROM appUsageLimit")
    LiveData<List<AppUsageLimit>> getAppUsageLimits();

    @Delete
    void deleteAppUsageLimit(AppUsageLimit appUsageLimit);

    @Query("DELETE FROM appUsageLimit")
    void deleteAllAppUsageLimit();
}
