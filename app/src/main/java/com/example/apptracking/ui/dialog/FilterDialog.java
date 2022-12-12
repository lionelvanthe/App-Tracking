package com.example.apptracking.ui.dialog;

import android.os.Bundle;
import android.view.View;
import com.example.apptracking.R;
import com.example.apptracking.databinding.DialogFilterBinding;
import com.example.apptracking.ui.base.BaseBindingDialogFragment;

public class FilterDialog extends BaseBindingDialogFragment<DialogFilterBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_filter;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

    }
}
