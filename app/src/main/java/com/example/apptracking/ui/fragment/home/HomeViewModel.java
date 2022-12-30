package com.example.apptracking.ui.fragment.home;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.apptracking.R;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.repository.UsageTimeRepository;
import com.example.apptracking.ui.base.BaseViewModel;
import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel {

    private UsageTimeRepository repository;

    private MutableLiveData<List<App>> _apps = new MutableLiveData<>();
    public LiveData<List<App>> apps  = _apps;

    private MutableLiveData<Boolean> _isSortDataComplete = new  MutableLiveData<>();
    public LiveData<Boolean> isSortDataComplete = _isSortDataComplete;

    private MutableLiveData<List<Float>> _listUsageTimePerHourOfDevice = new MutableLiveData<>();
    LiveData<List<Float>> listUsageTimePerHourOfDevice  = _listUsageTimePerHourOfDevice;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);

    }

    public void getApps() {
        _isSortDataComplete.postValue(false);
        repository.getApps().subscribe(new SingleObserver<List<App>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<App> apps) {
                sortData(apps);
                _apps.postValue(apps);
                _isSortDataComplete.postValue(true);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                _isSortDataComplete.postValue(false);
            }
        });
    }


    public void getListUsageTimePerHourOfDevice() {

        repository.getListUsageTimePerHourOfDevice().subscribe(new SingleObserver<ArrayList<Float>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull ArrayList<Float> listUsageTimePerHourOfDevice) {
                _listUsageTimePerHourOfDevice.postValue(listUsageTimePerHourOfDevice);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    private void sortData(List<App> apps) {
        int currentSortType = Hawk.get(Const.SORT_TYPE, 0);
        if (currentSortType == 0) {
            Collections.sort(apps, (o1, o2) -> Long.compare(o2.getUsageTimeOfDay(), o1.getUsageTimeOfDay()));
        } else if (currentSortType == 1) {
            Collections.sort(apps, (o1, o2) -> Long.compare(o1.getUsageTimeOfDay(), o2.getUsageTimeOfDay()));
        } else if (currentSortType == 2) {
            Collections.sort(apps, (o1, o2) -> Collator.getInstance(new Locale("vi", "VN"))
                    .compare(o1.getName().toLowerCase(), o2.getName().toLowerCase()));
        } else {
            Collections.sort(apps, (o1, o2) -> Collator.getInstance(new Locale("vi", "VN"))
                    .compare(o2.getName().toLowerCase(), o1.getName().toLowerCase()));
        }
    }

    public long getTotalUsageTime() {
        return repository.getTotalUsageTime();
    }
}
