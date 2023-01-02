package com.example.apptracking.ui.fragment.more;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.repository.UsageTimeRepository;
import com.example.apptracking.ui.base.BaseViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class MoreViewModel extends BaseViewModel {

    private UsageTimeRepository usageTimeRepository;

    private MutableLiveData<List<Float>> _deviceUnlocksPerHour = new MutableLiveData<>();
    public LiveData<List<Float>> deviceUnlocksPerHour = _deviceUnlocksPerHour;

    private MutableLiveData<List<Float>> _listUsageTimePerHourOfDevice = new MutableLiveData<>();
    LiveData<List<Float>> listUsageTimePerHourOfDevice  = _listUsageTimePerHourOfDevice;

    private MutableLiveData<List<App>> _apps = new MutableLiveData<>();
    public LiveData<List<App>> apps  = _apps;

    public MoreViewModel(@NonNull Application application) {
        super(application);
        UsageTime usageTime = UsageTime.getInstance(application);
        usageTimeRepository = new UsageTimeRepository(usageTime);

        getListDeviceUnlocksPerHour();
        getListUsageTimePerHourOfDevice();
        getApps();
    }

    public void getListDeviceUnlocksPerHour() {

        usageTimeRepository.getListDeviceUnlocksPerHour().subscribe(new SingleObserver<ArrayList<Float>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull ArrayList<Float> getListDeviceUnlocksPerHour) {
                _deviceUnlocksPerHour.postValue(getListDeviceUnlocksPerHour);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getListUsageTimePerHourOfDevice() {

        usageTimeRepository.getListUsageTimePerHourOfDevice().subscribe(new SingleObserver<ArrayList<Float>>() {
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

    public void getApps() {
        usageTimeRepository.getApps().subscribe(new SingleObserver<List<App>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<App> apps) {
                Collections.sort(apps, (o1, o2) -> Long.compare(o2.getUsageTimeOfDay(), o1.getUsageTimeOfDay()));
                _apps.postValue(apps);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    private void getCategory(String packageName){
        String GOOGLE_URL = "https://play.google.com/store/apps/details?id=" + packageName;

        try {
            Document doc  = Jsoup.connect(GOOGLE_URL).get();
            int index = doc.body().data().indexOf("applicationCategory");
            String simpleString = doc.body().data().subSequence(index,index+100).toString();
            String data =  simpleString.split(":")[1].split(",")[0];
            Log.d("Thenv", "getCategory: " + data + ", packageName: " + packageName);
        } catch (Exception e ) {
            Log.e("DATA-->",e.toString());
        }


    }


    public float getMaxDeviceUnlocks() {
        return usageTimeRepository.getMaxDeviceUnlocks();
    }

    public long getTotalUsageTime() {
        return usageTimeRepository.getTotalUsageTime();
    }

    public int getDeviceUnlocks() {
        return usageTimeRepository.getDeviceUnlocks();
    }

    public int getNotificationReceives() {
        return usageTimeRepository.getNotificationReceives();
    }

}
