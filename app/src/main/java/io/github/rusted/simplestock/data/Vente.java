package io.github.rusted.simplestock.data;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vente {
    private int numProduit;
    private String design;
    private double prix;
    private double quantite;

    public double montant() {
        return prix * quantite;
    }
}
