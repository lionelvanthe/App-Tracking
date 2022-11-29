package com.example.apptracking.ui.fragment.usagelimits;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.data.repository.UsageTimeRepository;
import com.example.apptracking.ui.base.BaseViewModel;
import com.example.apptracking.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class UsageLimitsViewModel extends BaseViewModel {

    private UsageTimeRepository repository;
    private Application application;

    private MutableLiveData<List<AppUsageLimit>> _appUsageLimits = new MutableLiveData<>();
    public LiveData<List<AppUsageLimit>>  appUsageLimits  = _appUsageLimits;

    public UsageLimitsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);
    }

    public void getAppUsageTimeLimit() {

        repository.getAppsInstalled(application).subscribe(new SingleObserver<List<AppUsageLimit>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<AppUsageLimit> apps) {
                _appUsageLimits.setValue(apps);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }
}
