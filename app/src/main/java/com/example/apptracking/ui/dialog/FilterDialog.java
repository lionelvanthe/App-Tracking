package com.example.apptracking.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import com.example.apptracking.R;
import com.example.apptracking.databinding.DialogFilterBinding;
import com.example.apptracking.ui.base.BaseBindingDialogFragment;
import com.example.apptracking.utils.Const;
import com.orhanobut.hawk.Hawk;

public class FilterDialog extends BaseBindingDialogFragment<DialogFilterBinding> {

    private CalendarDialog calendarDialog;

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
                if (calendarDialog == null) {
                    bundle.putLong(Const.START_TIME, 0);
                    bundle.putLong(Const.END_TIME, 0);
                } else {
                    bundle.putLong(Const.START_TIME, calendarDialog.getStartTime());
                    bundle.putLong(Const.END_TIME, calendarDialog.getEndTime());
                }
                getParentFragmentManager().setFragmentResult(Const.FILTER_KEY, bundle);
            }
        });

        binding.icCalender.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            calendarDialog = new CalendarDialog();
            calendarDialog.show(getParentFragmentManager(), "calendar_dialog");
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
}
