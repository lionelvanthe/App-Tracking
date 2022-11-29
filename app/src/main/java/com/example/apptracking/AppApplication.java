package com.example.apptracking;

import android.app.Application;

import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;

public class AppApplication extends android.app.Application {
    private static volatile AppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        getInstance();
        Hawk.init(getApplicationContext()).build();
        Hawk.put(Const.END_TIME_HOUR, -1);
    }

    public static AppApplication getInstance()
    {
        if (instance== null) {
            synchronized(AppApplication.class) {
                if (instance == null)
                    instance = new AppApplication();
            }
        }
        return instance;
    }
}
