package io.github.rusted.simplestock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.databinding.FragmentStockFormBinding;
import io.github.rusted.simplestock.enums.StockFormOperationMode;
import io.github.rusted.simplestock.util.FormatUtil;
import io.github.rusted.simplestock.viewmodel.StockViewModel;
import org.jetbrains.annotations.NotNull;

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
        StockFormFragmentArgs args = StockFormFragmentArgs.fromBundle(getArguments());
        Vente v = args.getVente();
        if (args.getOperation() == StockFormOperationMode.CREATE) setupCreateForm();
        else if (v != null) setupUpdateForm(v);

        binding.editTextDesign.addTextChangedListener(watcher);
        binding.editTextPrix.addTextChangedListener(watcher);
        binding.editTextQuantite.addTextChangedListener(watcher);
    }

    public void onSubmit(StockFormOperationMode operationMode) {
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        MutableLiveData<Void> success = operationMode == StockFormOperationMode.CREATE
                ? viewModel.venteCreated()
                : viewModel.venteUpdated();
        setLoading(true);
        viewModel.error().observe(getViewLifecycleOwner(), error -> setLoading(false));
        success.observe(getViewLifecycleOwner(), vente -> {
            setLoading(false);
            NavHostFragment.findNavController(this).popBackStack();
        });
        if (operationMode == StockFormOperationMode.UPDATE) viewModel.update(buildVente());
        else viewModel.create(buildVente());
    }

    private void setupUpdateForm(@NotNull Vente v) {
        binding.buttonSubmit.setText(R.string.button_modifier_label);
        binding.buttonSubmit.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_edit));
        binding.textViewMontant.setText(getString(R.string.montant_label, FormatUtil.format(v.montant())));
        binding.editTextDesign.setText(v.getDesign());
        binding.editTextPrix.setText(String.valueOf(v.getPrix()));
        binding.editTextQuantite.setText(String.valueOf(v.getQuantite()));
        binding.textViewNumProduit.setText(String.valueOf(v.getNumProduit()));
        binding.buttonSubmit.setOnClickListener(view -> onSubmit(StockFormOperationMode.UPDATE));
    }

    private void setupCreateForm() {
        binding.buttonSubmit.setText(R.string.ajouter_button_text);
        binding.textViewMontant.setText(getString(R.string.montant_label, "0,00"));
        binding.buttonSubmit.setOnClickListener(view -> onSubmit(StockFormOperationMode.CREATE));
    }

    private Vente buildVente() {
        Vente vente = Vente.builder()
                .design(String.valueOf(binding.editTextDesign.getText()))
                .prix(Double.parseDouble(String.valueOf(binding.editTextPrix.getText())))
                .quantite(Double.parseDouble(String.valueOf(binding.editTextQuantite.getText())))
                .build();
        if (binding.textViewNumProduit.getText().length() > 0)
            vente.setNumProduit(Integer.parseInt(binding.textViewNumProduit.getText().toString()));
        return vente;
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