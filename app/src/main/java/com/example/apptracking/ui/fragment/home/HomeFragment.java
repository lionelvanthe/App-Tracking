package com.example.apptracking.ui.fragment.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.example.apptracking.R;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.FragmentHomeBinding;
import com.example.apptracking.ui.activity.main.MainViewModel;
import com.example.apptracking.ui.adapter.AppUsageAdapter;
import com.example.apptracking.ui.base.BaseBindingFragment;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import com.orhanobut.hawk.Hawk;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding, HomeViewModel> {

    private AppUsageAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    protected Class<HomeViewModel> getViewModel() {
        return HomeViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mainViewModel == null) {
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        getParentFragmentManager().setFragmentResultListener(Const.FILTER_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                if (Hawk.get(Const.IS_TODAY)) {
                    viewModel.getApps();
                } else {
                    mainViewModel.getUsageTime(bundle.getLong(Const.START_TIME), bundle.getLong(Const.END_TIME));
                }
                Hawk.put(Const.IS_LOAD_DATA, true);
            }
        });

        adapter = new AppUsageAdapter(requireContext(),
                R.layout.item_app_layout,
                model -> {
                    NavDirections action = HomeFragmentDirections.actionNavigationHomeToAppDetailFragment(model);
                    Navigation.findNavController(view).navigate(action);
        });
    }
    
    @Override
    protected void setupListener() {
        binding.layoutLinaerMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = HomeFragmentDirections.actionNavigationHomeToMoreFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.layoutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = HomeFragmentDirections.actionNavigationHomeToFilterDialog();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    @Override
    protected void setupObserver() {

        mainViewModel.isGetDataComplete.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && Hawk.get(Const.IS_LOAD_DATA, true)) {
                    viewModel.getApps();
                }
            } 
        });

        viewModel.apps.observe(getViewLifecycleOwner(), new Observer<List<App>>() {
            @Override
            public void onChanged(List<App> apps) {
                if (apps != null && Hawk.get(Const.IS_LOAD_DATA, true)) {
                    adapter.setListData(apps);
                    Log.d("Thenv", "onChanged: adfadfasdfasdfasdfasf");
                    adapter.setTotalUsageTime(viewModel.getTotalUsageTime());
                    binding.recyclerViewApp.setAdapter(adapter);
                    viewModel.getListUsageTimePerHourOfDevice();
                    binding.tvTotalUsageTimeContent.setText(Utils.formatMilliSeconds(viewModel.getTotalUsageTime()));
                    Hawk.put(Const.IS_LOAD_DATA, false);
                }
            }
        });

        viewModel.listUsageTimePerHourOfDevice.observe(this, new Observer<List<Float>>() {
            @Override
            public void onChanged(List<Float> floats) {
                CopyOnWriteArrayList<Float> arrayList = new CopyOnWriteArrayList<>(floats);
                binding.barView.setDataList(arrayList, 60);
            }
        });
        viewModel.isSortDataComplete.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progress.setVisibility(View.GONE);
                    binding.recyclerViewApp.setVisibility(View.VISIBLE);
                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.recyclerViewApp.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    protected void onPermissionGranted() {
    }
}
