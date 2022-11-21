package com.example.apptracking.ui.base;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

class BaseDiffCallback<T> extends DiffUtil.ItemCallback<T> {

    public BaseDiffCallback() {

    }

    @Override
    public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.toString().equals(newItem.toString());
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem == newItem;
    }

}
