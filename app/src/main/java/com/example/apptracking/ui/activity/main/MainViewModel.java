package com.example.apptracking.ui.activity.main;

import android.app.Application;

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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainViewModel extends BaseViewModel {

    private UsageTimeRepository repository;

    private MutableLiveData<Boolean> _isGetDataComplete = new  MutableLiveData<>();
    public LiveData<Boolean> isGetDataComplete = _isGetDataComplete;

    public MainViewModel(@NonNull Application application) {
        super(application);
        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);
    }

    public void getUsageTime(long startTime, long endTime) {

        _isGetDataComplete.postValue(false);
        repository.getUsageTime(startTime, endTime).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                _isGetDataComplete.postValue(true);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                _isGetDataComplete.postValue(false);
            }
        });
    }

}
