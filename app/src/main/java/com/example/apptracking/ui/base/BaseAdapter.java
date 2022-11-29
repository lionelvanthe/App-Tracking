package com.example.apptracking.ui.base;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptracking.interfaces.ItemClickListener;

import java.util.List;

abstract public class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder> {

    private int layoutId;
    public List<T> list;
    private ItemClickListener<T> itemClickListener;

    public BaseAdapter(int layoutId, ItemClickListener<T> itemClickListener) {
        this.layoutId = layoutId;
        this.itemClickListener = itemClickListener;
    }

    public void setListData(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    protected abstract BaseViewHolder setViewHolder(ViewDataBinding binding);

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, layoutId, parent, false);

        return setViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.BaseViewHolder holder, int position) {
        holder.bindData(list.get(position));
        holder.clickListener(list.get(position), itemClickListener);
    }
    abstract public class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());
        }

        protected abstract void bindData(T data);

        protected abstract void clickListener(T data, ItemClickListener<T> itemClickListener);

    }
}
