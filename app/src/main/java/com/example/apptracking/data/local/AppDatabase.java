package com.example.apptracking.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.apptracking.data.local.dao.AppUsageLimitDAO;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.utils.Const;

@Database(entities = {AppUsageLimit.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppUsageLimitDAO appUsageLimitDAO();

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, Const.DB_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }

        return instance;
    }
}
