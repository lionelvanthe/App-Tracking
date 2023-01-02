package com.example.apptracking.ui.fragment.search;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.apptracking.AppApplication;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;
import com.example.apptracking.data.repository.UsageTimeRepository;
import com.example.apptracking.ui.base.BaseViewModel;
import com.example.apptracking.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchViewModel extends BaseViewModel {

    private UsageTimeRepository repository;

    private MutableLiveData<List<App>> _apps = new MutableLiveData<>();
    public LiveData<List<App>> apps  = _apps;

    private List<App> appInstalled;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        UsageTime usageTime = UsageTime.getInstance(application);
        repository = new UsageTimeRepository(usageTime);
        appInstalled = new ArrayList<>();
        getListAppInstalled(application);

    }

    private void getListAppInstalled(Application application) {
        List<ApplicationInfo> applicationInfos = application.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        HashMap<String, App> appHashMap = getMapAppInToDay();
        Completable.fromRunnable(() -> {
            for (ApplicationInfo applicationInfo : applicationInfos) {
                if (AppApplication.getHashMapAppOpenableAndInstalled().get(applicationInfo.packageName) != null) {
                    String packageName = applicationInfo.packageName;
                    if (appHashMap.get(packageName) == null) {
                        App app = new App(Utils.parsePackageName(application.getPackageManager(), packageName), packageName);
                        appInstalled.add(app);
                    } else {
                        appInstalled.add(appHashMap.get(applicationInfo.packageName));
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe();
    }

    public void appsAfterFilter(String character) {
        Single.fromCallable(() -> filterListByName(character))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new SingleObserver<List<App>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<App> apps) {
                        _apps.postValue(apps);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        _apps.postValue(new ArrayList<>());
                    }
                });
    }

    private List<App> filterListByName(String character) {
        List<App> appsAfterFilter = new ArrayList<>();
        for (App a: appInstalled) {
            if (a.getName().toLowerCase().contains(character.toLowerCase())) {
                appsAfterFilter.add(a);
            }
        }
        return appsAfterFilter;
    }

    public HashMap<String, App> getMapAppInToDay() {
        return repository.getMapAppInToDay();
    }

    public long getTotalUsageTime() {
        return repository.getTotalUsageTime();
    }
}
