package com.example.apptracking.ui.fragment.usagelimits;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.apptracking.data.local.AppDatabase;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.local.dao.AppUsageLimitDAO;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.data.repository.AppUsageLimitRepository;
import com.example.apptracking.data.repository.UsageTimeRepository;
import com.example.apptracking.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class UsageLimitsViewModel extends BaseViewModel {

    private UsageTimeRepository usageTimeRepository;
    private AppUsageLimitRepository appUsageLimitRepository;
    private Application application;

    private MutableLiveData<List<AppUsageLimit>> _appUsageLimits = new MutableLiveData<>();
    public LiveData<List<AppUsageLimit>>  appUsageLimits  = _appUsageLimits;

    private MutableLiveData<List<String>> _packageNames = new MutableLiveData<>();
    public LiveData<List<String>>  packageNames  = _packageNames;

    public LiveData<List<AppUsageLimit>> appUsageLimitsInDatabase;

    public UsageLimitsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        UsageTime usageTime = UsageTime.getInstance(application);
        usageTimeRepository = new UsageTimeRepository(usageTime);

        AppUsageLimitDAO dao = AppDatabase
                .getInstance(this.application.getApplicationContext())
                .appUsageLimitDAO();
        appUsageLimitRepository = new AppUsageLimitRepository(dao);
        appUsageLimitsInDatabase = appUsageLimitRepository.getAppUsageLimits();

        getPackageNames();
    }


    public void getPackageNames() {
        appUsageLimitRepository.getPackageNames().subscribe(new SingleObserver<List<String>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<String> strings) {
                _packageNames.postValue(strings);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                _packageNames.postValue(new ArrayList<>());
            }
        });
    }

    public void updateUsageTimeOfDay(long usageTime, String packageName) {

        appUsageLimitRepository.updateUsageTimeOfDay(usageTime, packageName).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {}

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    public long geUsageTimeFollowPackageName(String packageName) {
        return usageTimeRepository.geUsageTimeFollowPackageName(packageName);
    }

    public Float[] getUsageTimePerHourFollowPackageName(String packageName) {
        return usageTimeRepository.getUsageTimePerHourFollowPackageName(packageName);
    }

    public void getAppUsageTimeLimit() {

        usageTimeRepository.getAppsInstalled(application).subscribe(new SingleObserver<List<AppUsageLimit>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<AppUsageLimit> apps) {
                _appUsageLimits.postValue(apps);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }

    public void createAppUsageLimit(AppUsageLimit appUsageLimit) {
        appUsageLimitRepository.createAppUsageLimit(appUsageLimit);
    }

    public void deleteAppUsageLimit(AppUsageLimit appUsageLimit) {
        appUsageLimitRepository.deleteAppUsageLimit(appUsageLimit);
    }

}
