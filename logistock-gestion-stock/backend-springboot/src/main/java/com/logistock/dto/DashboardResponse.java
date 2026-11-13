package com.logistock.dto;

import java.math.BigDecimal;
import java.util.List;

/** Tableau de bord du stock : chiffres clés, produits en stock faible (les plus critiques d'abord) et derniers mouvements. */
public record DashboardResponse(
        long nombreProduits,
        long nombreProduitsStockFaible,
        long nombreRuptures,
        BigDecimal valeurStock,
        List<ProduitResponse> produitsStockFaible,
        List<MouvementResponse> derniersMouvements) {
}
