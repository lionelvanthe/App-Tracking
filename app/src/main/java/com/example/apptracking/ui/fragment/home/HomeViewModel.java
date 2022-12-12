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

    private MutableLiveData<List<Float>> _listUsageTimePerHourOfDevice = new MutableLiveData<>();
    LiveData<List<Float>> listUsageTimePerHourOfDevice  = _listUsageTimePerHourOfDevice;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);

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
