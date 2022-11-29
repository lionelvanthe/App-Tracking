package com.example.apptracking.ui.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.apptracking.utils.Utils;
import java.util.List;

public class AddUsageLimitDialog extends BaseBindingDialogFragment<DialogAddUsageLimitBinding> {

    private UsageLimitsViewModel viewModel;

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
        viewModel = new ViewModelProvider(this).get(UsageLimitsViewModel.class);
        viewModel.getAppUsageTimeLimit();
        binding.imgBgSpinnerApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopUpAppInstalled();
            }
        });

        setUpPopupWarningType();

        setValueOfNumberPicker(binding.numberPickerHours, 12, getResources().getStringArray(R.array.hours));
        setValueOfNumberPicker(binding.numberPickerMinus, 59, getResources().getStringArray(R.array.minus));
        Log.d("Thenv", "onCreatedView: " + ConstraintLayout.LayoutParams.MATCH_PARENT);
        binding.spinnerWarningType.setDropDownWidth(ConstraintLayout.LayoutParams.MATCH_PARENT);
        binding.edtOptionalText.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.white));

        setUpListener();

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

            }
        });
    }


    private void setValueOfNumberPicker(NumberPicker numberPicker, int maxValue, String[] values) {
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setDisplayedValues(values);
    }

    private void setUpPopupWarningType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.warning_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerWarningType.setAdapter(adapter);

        binding.spinnerWarningType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpPopUpAppInstalled() {

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());

        MenuSpinnerAppBinding bd = DataBindingUtil.inflate(layoutInflater, R.layout.menu_spinner_app, null, false);

        PopupWindow popUp = new PopupWindow(
                bd.getRoot(), this.binding.imgBgSpinnerApplication.getWidth(),
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popUp.setTouchable(true);
        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.setElevation(10f);

        AppAdapter adapter = new AppAdapter(requireContext(), R.layout.item_spinner_app_layout, new ItemClickListener<AppUsageLimit>() {
            @Override
            public void onClickListener(AppUsageLimit model) {
                binding.imgIconApp.setBackground(Utils.getPackageIcon(requireContext(), model.getPackageName()));
                binding.tvAppName.setText(model.getName());
                popUp.dismiss();
            }
        });

        viewModel.appUsageLimits.observe(getViewLifecycleOwner(), new Observer<List<AppUsageLimit>>() {
            @Override
            public void onChanged(List<AppUsageLimit> appUsageLimits) {
                adapter.setListData(appUsageLimits);
                bd.recyclerViewApplication.setAdapter(adapter);
                popUp.showAsDropDown(binding.imgBgSpinnerApplication);
            }
        });
    }

}
