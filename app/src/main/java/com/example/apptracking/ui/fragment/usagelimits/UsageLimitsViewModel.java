package com.example.apptracking.ui.fragment.usagelimits;

import android.app.Application;

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

import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class UsageLimitsViewModel extends BaseViewModel {

    private UsageTimeRepository usageTimeRepository;
    private AppUsageLimitRepository appUsageLimitRepository;
    private Application application;


    private MutableLiveData<List<AppUsageLimit>> _appUsageLimits = new MutableLiveData<>();
    public LiveData<List<AppUsageLimit>>  appUsageLimits  = _appUsageLimits;

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
    }

    public void getUsageTimeToday() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeNow);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(timeNow);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);
        cal2.set(Calendar.MILLISECOND, 99);

        usageTimeRepository.getUsageTimeOfApps(cal.getTimeInMillis(), cal2.getTimeInMillis()).subscribe();
    }

    public long getTotalUsageTime() {
        return usageTimeRepository.getTotalUsageTime();
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

    public void deleteAllAppUsageLimit() {
        appUsageLimitRepository.deleteAllAppUsageLimit();
    }

}
