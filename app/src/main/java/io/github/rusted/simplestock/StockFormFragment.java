package io.github.rusted.simplestock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.databinding.FragmentStockFormBinding;
import io.github.rusted.simplestock.enums.StockFormOperationMode;
import io.github.rusted.simplestock.util.FormatUtil;
import io.github.rusted.simplestock.viewmodel.StockViewModel;

import java.util.Objects;

public class StockFormFragment extends Fragment {

    private FragmentStockFormBinding binding;

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Editable quantite = binding.editTextQuantite.getText();
            Editable prix = binding.editTextPrix.getText();
            Editable design = binding.editTextDesign.getText();
            boolean allFilled = quantite != null && !quantite.toString().isBlank()
                                && prix != null && !prix.toString().isBlank()
                                && design != null && !design.toString().isBlank();
            binding.buttonSubmit.setEnabled(allFilled);
            double montant = 0;
            if (allFilled) {
                montant = Double.parseDouble(quantite.toString()) * Double.parseDouble(prix.toString());
            }
            binding.textViewMontant.setText(getString(R.string.montant_label, FormatUtil.format(montant)));
        }
    };


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentStockFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StockFormOperationMode mode = StockFormFragmentArgs.fromBundle(getArguments()).getOperation();
        if (mode == StockFormOperationMode.CREATE) {
            Objects.requireNonNull(
                    NavHostFragment.findNavController(StockFormFragment.this).getCurrentDestination()
            ).setLabel("Créer une vente");
            binding.buttonSubmit.setText(R.string.ajouter_button_text);
        }
        binding.textViewMontant.setText(getString(R.string.montant_label, "0,00"));
        binding.buttonSubmit.setOnClickListener(this::onSubmit);
        binding.editTextDesign.addTextChangedListener(watcher);
        binding.editTextPrix.addTextChangedListener(watcher);
        binding.editTextQuantite.addTextChangedListener(watcher);
    }

    public void onSubmit(View ignoredView) {
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        setLoading(true);
        viewModel.error().observe(getViewLifecycleOwner(), error -> setLoading(false));
        viewModel.venteCreated().observe(getViewLifecycleOwner(), vente -> {
            setLoading(false);
            NavHostFragment.findNavController(StockFormFragment.this).popBackStack();
        });
        viewModel.create(Vente.builder()
                .design(String.valueOf(binding.editTextDesign.getText()))
                .prix(Double.parseDouble(String.valueOf(binding.editTextPrix.getText())))
                .quantite(Double.parseDouble(String.valueOf(binding.editTextQuantite.getText())))
                .build()
        );
    }

    void setLoading(boolean loading) {
        binding.loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.formContent.setClickable(!loading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}