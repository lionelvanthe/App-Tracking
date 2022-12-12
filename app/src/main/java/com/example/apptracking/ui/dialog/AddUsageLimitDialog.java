package com.example.apptracking.ui.dialog;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.apptracking.R;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.databinding.DialogAddUsageLimitBinding;
import com.example.apptracking.databinding.MenuSpinnerAppBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.adapter.AppAdapter;
import com.example.apptracking.ui.base.BaseBindingDialogFragment;
import com.example.apptracking.ui.fragment.usagelimits.UsageLimitsViewModel;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;

import java.util.Calendar;
import java.util.List;

public class AddUsageLimitDialog extends BaseBindingDialogFragment<DialogAddUsageLimitBinding> {

    private UsageLimitsViewModel viewModel;
    private AppUsageLimit appUsageLimit;
    private AppAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_add_usage_limit;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        setValueOfNumberPicker(binding.numberPickerHours, 12, getResources().getStringArray(R.array.hours));
        setValueOfNumberPicker(binding.numberPickerMinus, 58, getResources().getStringArray(R.array.minus));
        binding.textInputLayoutOptionalText.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.white));

        viewModel = new ViewModelProvider(this).get(UsageLimitsViewModel.class);
        viewModel.getAppUsageTimeLimit();

        initView();

        if (!appUsageLimit.getName().equals(Const.TOTAL_USAGE)) {
            setUpPopupWarningType(R.array.warning_type);
        } else {
            setUpPopupWarningType(R.array.waring_type_total_usage);
        }
        binding.spinnerWarningType.setSelection(appUsageLimit.getWarningType());
        setUpListener();
    }

    private void initView() {
        appUsageLimit = AddUsageLimitDialogArgs.fromBundle(getArguments()).getAppUsageLimit();
        if (appUsageLimit== null) {
            appUsageLimit = new AppUsageLimit(Const.TOTAL_USAGE, "com.example.totalusage");
            appUsageLimit.setUsageTimeOfDay(viewModel.getTotalUsageTime());
            binding.cardView.setVisibility(View.GONE);
            binding.tvTitle.setText(getString(R.string.add_a_usage_limit));
            binding.imgBgSpinnerApplication.setVisibility(View.VISIBLE);
            binding.cardView2.setVisibility(View.VISIBLE);
            binding.tvApplication.setVisibility(View.VISIBLE);
            binding.tvAppName.setVisibility(View.VISIBLE);
            binding.icExpandApp.setVisibility(View.VISIBLE);
            binding.icDeleteItem.setVisibility(View.GONE);
        } else {
            binding.iconApp.setBackground(Utils.getPackageIcon(requireContext(), appUsageLimit.getPackageName()));
            binding.tvTitle.setText(appUsageLimit.getName());
            binding.imgBgSpinnerApplication.setVisibility(View.GONE);
            binding.cardView2.setVisibility(View.GONE);
            binding.tvApplication.setVisibility(View.GONE);
            binding.tvAppName.setVisibility(View.GONE);
            binding.icExpandApp.setVisibility(View.GONE);
            binding.textInputOptionalText.setText(appUsageLimit.getTextDisplayed());
            binding.icDeleteItem.setVisibility(View.VISIBLE);

            int[] hourAndMinus = millisToTime(appUsageLimit.getUsageTimeLimit());
            binding.numberPickerHours.setValue(hourAndMinus[0]);
            binding.numberPickerMinus.setValue(hourAndMinus[1] - 1);

        }
    }

    private void setUpListener() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                updateDatabase();
                setUsageTimeLimit();
            }
        });

        binding.imgBgSpinnerApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopUpAppInstalled();
            }
        });

        binding.icDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteAppUsageLimit(appUsageLimit);
                dismiss();
            }
        });
    }

    private void setUsageTimeLimit() {
        AlarmManager manager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        appUsageLimit.setAlarm(requireContext(), manager);
    }

    private void updateDatabase() {
        long timeLimit = timeToMillis(binding.numberPickerHours.getValue(), binding.numberPickerMinus.getValue() + 1);
        appUsageLimit.setUsageTimeLimit(timeLimit);
        appUsageLimit.setWarningType(binding.spinnerWarningType.getSelectedItemPosition());
        appUsageLimit.setTextDisplayed(binding.textInputOptionalText.getText().toString());
        viewModel.createAppUsageLimit(appUsageLimit);
    }

    private long timeToMillis(int hours, int minus) {
        long second = hours*3600L + minus* 60L;
        return second*1000;
    }

    private int[] millisToTime(long millis) {
        int hours;
        int minus;
        long second = millis/1000;
        hours = (int) (second/3600);
        minus = (int) (second%3600)/60;

        return new int[]{hours, minus};
    }


    private void setValueOfNumberPicker(NumberPicker numberPicker, int maxValue, String[] values) {
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setDisplayedValues(values);
    }

    private void setUpPopupWarningType(int textArrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                textArrayResId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerWarningType.setAdapter(adapter);
    }

    private void setUpPopUpAppInstalled() {

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());

        MenuSpinnerAppBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.menu_spinner_app, null, false);

        PopupWindow popUp = new PopupWindow(
                binding.getRoot(), this.binding.imgBgSpinnerApplication.getWidth(),
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popUp.setTouchable(true);
        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.setElevation(10f);
        setUpAdapterInPopUp(popUp);
        observerDataInPopup(popUp, binding);
    }

    private void setUpAdapterInPopUp(PopupWindow popUp) {
        adapter = new AppAdapter(requireContext(), R.layout.item_spinner_app_layout, new ItemClickListener<AppUsageLimit>() {
            @Override
            public void onClickListener(AppUsageLimit model) {
                if (!model.getName().equals(Const.TOTAL_USAGE)) {
                    appUsageLimit = model;
                    setUpPopupWarningType(R.array.warning_type);
                } else {
                    setUpPopupWarningType(R.array.waring_type_total_usage);
                }
                binding.imgIconApp.setBackground(Utils.getPackageIcon(requireContext(), model.getPackageName()));
                binding.tvAppName.setText(model.getName());
                popUp.dismiss();
            }
        });
    }

    private void observerDataInPopup(PopupWindow popUp, MenuSpinnerAppBinding bd) {
        viewModel.appUsageLimits.observe(getViewLifecycleOwner(), new Observer<List<AppUsageLimit>>() {
            @Override
            public void onChanged(List<AppUsageLimit> appUsageLimits) {
                if (!appUsageLimits.get(0).getName().equals(Const.TOTAL_USAGE)) {
                    appUsageLimits.add(0, appUsageLimit);
                }
                adapter.setListData(appUsageLimits);
                bd.recyclerViewApplication.setAdapter(adapter);
                popUp.showAsDropDown(binding.imgBgSpinnerApplication);
            }
        });
    }

}
