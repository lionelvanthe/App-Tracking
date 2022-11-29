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

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel {

    private UsageTimeRepository repository;
    private Application application;

    private MutableLiveData<List<App>> _apps = new MutableLiveData<>();
    LiveData<List<App>> apps  = _apps;

    private MutableLiveData<Boolean> _isGetDataComplete = new  MutableLiveData<>();
    LiveData<Boolean> isGetDataComplete = _isGetDataComplete;

    private MutableLiveData<List<Float>> _listUsageTimePerHourOfDevice = new MutableLiveData<>();
    LiveData<List<Float>> listUsageTimePerHourOfDevice  = _listUsageTimePerHourOfDevice;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);

    }

    public void getListApp(long startTime, long endTime) {

        _isGetDataComplete.postValue(false);
        repository.getUsageTimeOfApps(startTime, endTime).subscribe(new SingleObserver<List<App>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<App> apps) {
                sortData(apps);
                _apps.postValue(apps);
                _isGetDataComplete.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                _isGetDataComplete.postValue(false);
            }
        });
    }

    private void sortData(List<App> apps) {
        String currentSortType = Hawk.get(Const.SORT_TYPE, application.getString(R.string.usage_time_largest));

        if (Objects.equals(currentSortType, application.getString(R.string.usage_time_largest))) {
            Collections.sort(apps, (o1, o2) -> Long.compare(o2.getUsageTimeOfDay(), o1.getUsageTimeOfDay()));
        } else if (Objects.equals(currentSortType, application.getString(R.string.usage_time_smallest))) {
            Collections.sort(apps, (o1, o2) -> Long.compare(o1.getUsageTimeOfDay(), o2.getUsageTimeOfDay()));
        } else if (Objects.equals(currentSortType, application.getString(R.string.name_a_to_z))) {
            Collections.sort(apps, (o1, o2) -> Collator.getInstance(new Locale("vi", "VN"))
                    .compare(o1.getName().toLowerCase(), o2.getName().toLowerCase()));
        } else {
            Collections.sort(apps, (o1, o2) -> Collator.getInstance(new Locale("vi", "VN"))
                    .compare(o2.getName().toLowerCase(), o1.getName().toLowerCase()));
        }
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

    public long getTotalUsageTime() {
        return repository.getTotalUsageTime();
    }
}
