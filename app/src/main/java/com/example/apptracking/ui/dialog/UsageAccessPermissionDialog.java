package com.example.apptracking.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.apptracking.R;
import com.example.apptracking.databinding.DialogUsageAccessPermissionBinding;
import com.example.apptracking.ui.base.BaseBindingDialogFragment;

public class UsageAccessPermissionDialog extends BaseBindingDialogFragment<DialogUsageAccessPermissionBinding> {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Dialog85Percent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_usage_access_permission;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        binding.tvGrantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
    }
}
