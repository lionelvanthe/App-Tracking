package com.example.apptracking.interfaces;

import androidx.databinding.ViewDataBinding;

public interface RecyclerBinding<V extends ViewDataBinding, T extends Object> {
    void binData(V binder, T model, int position, ItemClickListener<T> itemClickListener);
}