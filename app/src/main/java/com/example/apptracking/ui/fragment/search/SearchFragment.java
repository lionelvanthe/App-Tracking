package com.example.apptracking.ui.fragment.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.example.apptracking.R;
import com.example.apptracking.data.model.App;
import com.example.apptracking.databinding.FragmentSearchBinding;
import com.example.apptracking.interfaces.ItemClickListener;
import com.example.apptracking.ui.adapter.AppUsageAdapter;
import com.example.apptracking.ui.base.BaseBindingFragment;
import java.util.List;

public class SearchFragment extends BaseBindingFragment<FragmentSearchBinding, SearchViewModel> {

    private AppUsageAdapter adapter;

    @Override
    protected Class<SearchViewModel> getViewModel() {
        return SearchViewModel.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        setTextColorHint();
        setupSearchView();

        adapter = new AppUsageAdapter(requireContext(), R.layout.item_app_layout, new ItemClickListener<App>() {
            @Override
            public void onClickListener(App model) {
                NavDirections action = SearchFragmentDirections.actionSearchFragmentToAppDetailFragment(model);
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void setTextColorHint(){
        SearchView.SearchAutoComplete theTextArea = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        theTextArea.setTextColor(Color.WHITE);
        theTextArea.setHintTextColor(Color.GRAY);
        theTextArea.setTextSize(16f);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.appsAfterFilter(newText);
                return true;
            }
        });
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager hideMe = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            hideMe.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void setupListener() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    @Override
    protected void setupObserver() {
        viewModel.apps.observe(getViewLifecycleOwner(), new Observer<List<App>>() {
            @Override
            public void onChanged(List<App> apps) {
                if (apps.isEmpty()) {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.recyclerviewApp.setVisibility(View.GONE);
                } else {
                    binding.tvNoData.setVisibility(View.GONE);
                    binding.recyclerviewApp.setVisibility(View.VISIBLE);
                    adapter.setListData(apps);
                    adapter.setTotalUsageTime(viewModel.getTotalUsageTime());
                    binding.recyclerviewApp.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    protected void onPermissionGranted() {

    }
}
