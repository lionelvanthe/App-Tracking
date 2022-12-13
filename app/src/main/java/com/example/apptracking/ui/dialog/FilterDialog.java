package com.example.apptracking.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.example.apptracking.R;
import com.example.apptracking.databinding.DialogCalendarBinding;
import com.example.apptracking.databinding.DialogFilterBinding;
import com.example.apptracking.ui.base.BaseBindingDialogFragment;
import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;
import java.util.Calendar;

public class FilterDialog extends BaseBindingDialogFragment<DialogFilterBinding> {


    private static int day = 0;
    private static int month = 0;
    private static int year = 0;

    private long startTime;
    protected long endTime;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_filter;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        setUpPopupSortType();
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
                Hawk.put(Const.SORT_TYPE, binding.spinnerSortType.getSelectedItemPosition());

                Bundle bundle = new Bundle();
                bundle.putLong(Const.START_TIME, startTime);
                bundle.putLong(Const.END_TIME, endTime);
                getParentFragmentManager().setFragmentResult(Const.FILTER_KEY, bundle);
            }
        });

        binding.icCalender.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showCalendarPicker();
        }
    });
}

    private void setUpPopupSortType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSortType.setAdapter(adapter);
        binding.spinnerSortType.setSelection(Hawk.get(Const.SORT_TYPE, 0));
    }

    private void showCalendarPicker() {
        Dialog dialog = new Dialog(requireActivity(), R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
        DialogCalendarBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_calendar, null, false);
        dialog.setContentView(binding.getRoot());

        if (day != 0) {
            binding.calendar.setDate(converterDateToMillis(day, month, year));
        }
        binding.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Hawk.put(Const.IS_TODAY, DateUtils.isToday(converterDateToMillis(dayOfMonth, month, year)));
                setEndTime(dayOfMonth, month, year);
                setStartTime(dayOfMonth, month, year);
                day = dayOfMonth;
                FilterDialog.month = month;
                FilterDialog.year = year;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private long converterDateToMillis(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }

    private void setStartTime(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startTime = calendar.getTimeInMillis();
    }

    private void setEndTime(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 99);
        endTime = calendar.getTimeInMillis();
    }
}
