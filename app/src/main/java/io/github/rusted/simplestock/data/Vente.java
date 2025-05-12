package io.github.rusted.simplestock.data;

import androidx.annotation.Nullable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vente implements Serializable {
    private int numProduit;
    private String design;
    private double prix;
    private double quantite;

    public double montant() {
        return prix * quantite;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return obj instanceof Vente
               && ((Vente) obj).numProduit == numProduit
               && Objects.equals(((Vente) obj).design, design)
               && ((Vente) obj).prix == prix
               && ((Vente) obj).quantite == quantite;
    }
}
