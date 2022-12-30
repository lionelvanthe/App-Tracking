package com.example.apptracking.ui.fragment.appdetail;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.repository.UsageTimeRepository;
import com.example.apptracking.ui.base.BaseViewModel;

import java.util.HashMap;

public class AppDetailViewModel extends BaseViewModel {

    private UsageTimeRepository repository;

    public AppDetailViewModel(@NonNull Application application) {
        super(application);

        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);
    }

    public int getNotificationReceive(String packageName) {
        return repository.getNotificationReceive(packageName);
    }

}
