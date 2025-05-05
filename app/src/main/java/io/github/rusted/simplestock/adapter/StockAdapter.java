package io.github.rusted.simplestock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.github.rusted.simplestock.R;
import io.github.rusted.simplestock.data.Vente;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private final List<Vente> ventes;

    public StockAdapter(List<Vente> ventes) {
        this.ventes = ventes;
    }

    @NonNull
    @NotNull
    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StockAdapter.ViewHolder viewHolder, int i) {
        Vente vente = ventes.get(i);
        Context context = viewHolder.itemView.getContext();
        viewHolder.design.setText(context.getString(R.string.design, vente.getDesign()));
        viewHolder.prix.setText(context.getString(R.string.prix, vente.getPrix()));
        viewHolder.quantite.setText(context.getString(R.string.quantite, (int) vente.getQuantite()));
        viewHolder.montant.setText(context.getString(R.string.montant, vente.montant()));
    }

    @Override
    public int getItemCount() {
        return ventes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView design, prix, quantite, montant;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            design = itemView.findViewById(R.id.design);
            prix = itemView.findViewById(R.id.prix);
            quantite = itemView.findViewById(R.id.quantite);
            montant = itemView.findViewById(R.id.montant);
        }
    }
}
