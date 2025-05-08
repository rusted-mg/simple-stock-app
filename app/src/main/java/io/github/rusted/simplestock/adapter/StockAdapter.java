package io.github.rusted.simplestock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import io.github.rusted.simplestock.R;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.databinding.StockItemBinding;
import io.github.rusted.simplestock.util.FormatUtil;
import org.jetbrains.annotations.NotNull;

public class StockAdapter extends ListAdapter<Vente, StockAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Vente> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Vente oldItem, @NonNull @NotNull Vente newItem) {
            return oldItem.getNumProduit() == newItem.getNumProduit();
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Vente oldItem, @NonNull @NotNull Vente newItem) {
            return oldItem.equals(newItem);
        }
    };

    public StockAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @NotNull
    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        StockItemBinding binding = StockItemBinding.inflate(inflater, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StockAdapter.ViewHolder viewHolder, int i) {
        Vente vente = getItem(i);
        viewHolder.bind(vente);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final StockItemBinding binding;

        public ViewHolder(@NotNull StockItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NotNull Vente vente) {
            Context context = itemView.getContext();
            binding.design.setText(context.getString(R.string.design, vente.getDesign()));
            binding.prix.setText(context.getString(R.string.prix, FormatUtil.format(vente.getPrix())));
            binding.quantite.setText(context.getString(R.string.quantite, FormatUtil.format(vente.getQuantite())));
            binding.montant.setText(context.getString(R.string.montant, FormatUtil.format(vente.montant())));
        }
    }
}
