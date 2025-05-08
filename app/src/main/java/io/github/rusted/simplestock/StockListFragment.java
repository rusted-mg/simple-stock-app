package io.github.rusted.simplestock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.github.rusted.simplestock.adapter.StockAdapter;
import io.github.rusted.simplestock.databinding.FragmentStockListBinding;
import io.github.rusted.simplestock.enums.StockFormOperationMode;
import io.github.rusted.simplestock.viewmodel.StockViewModel;

public class StockListFragment extends Fragment {

    private FragmentStockListBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentStockListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabAddProduct.setOnClickListener(v -> {
            var action = StockListFragmentDirections.actionStockListToStockForm();
            action.setOperation(StockFormOperationMode.CREATE);
            NavHostFragment.findNavController(StockListFragment.this).navigate(action);
        });
        StockAdapter stockAdapter = new StockAdapter();
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        binding.recyclerviewStock.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerviewStock.setAdapter(stockAdapter);
        viewModel.ventes().observe(getViewLifecycleOwner(), stockAdapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}