package io.github.rusted.simplestock;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.github.rusted.simplestock.adapter.StockAdapter;
import io.github.rusted.simplestock.data.ConnectionProvider;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.data.VenteRepository;
import io.github.rusted.simplestock.databinding.FragmentStockListBinding;
import io.github.rusted.simplestock.util.AndroidConfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class StockListFragment extends Fragment {

    private FragmentStockListBinding binding;
    private final List<Vente> ventes = new ArrayList<>();
    private final StockAdapter stockAdapter = new StockAdapter(ventes);

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
        binding.fabAddProduct.setOnClickListener(v ->
                NavHostFragment.findNavController(StockListFragment.this)
                        .navigate(R.id.action_StockList_to_SecondFragment)
        );
        binding.recyclerviewStock.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerviewStock.setAdapter(stockAdapter);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Vente> fetched = new VenteRepository(new ConnectionProvider(new AndroidConfig(this.getContext()))).all();
                Log.i("SimpleStock", "Fetched: " + fetched.size() + " ventes");
                requireActivity().runOnUiThread(() -> {
                    ventes.clear();
                    ventes.addAll(fetched);
                    stockAdapter.notifyItemRangeChanged(0, fetched.size());
                });
            } catch (SQLException e) {
                Log.e("SimpleStock", "onViewCreated: Failed to fetch Ventes", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}