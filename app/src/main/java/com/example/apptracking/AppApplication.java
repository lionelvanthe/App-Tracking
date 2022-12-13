package com.example.apptracking;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.orhanobut.hawk.Hawk;
import java.util.HashMap;
import java.util.List;

public class AppApplication extends android.app.Application {
    private static volatile AppApplication instance;
    private static HashMap<String, Boolean> hashMapAppOpenableAndInstalled;
    private static HashMap<String, Integer> hashMapDomainColor;

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(getApplicationContext()).build();

        Hawk.put(Const.IS_RETURN_FROM_BACKGROUND, true);
        Hawk.put(Const.IS_TODAY, true);
        initHashMap();
    }

    public static HashMap<String, Boolean> getHashMapAppOpenableAndInstalled() {
        return hashMapAppOpenableAndInstalled;
    }

    public static HashMap<String, Integer> getHashMapDomainColor() {
        return hashMapDomainColor;
    }

    private void initHashMap() {
        hashMapAppOpenableAndInstalled = new HashMap<>();
        hashMapDomainColor = new HashMap<>();
        PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app: packages) {
            if (Utils.isInstalled(pm, app.packageName) && Utils.openable(pm, app.packageName)) {
                hashMapAppOpenableAndInstalled.put(app.packageName, true);
            }
        }
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
