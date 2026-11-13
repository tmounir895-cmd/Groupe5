package com.logistock.dto;

import java.math.BigDecimal;

import com.logistock.entity.Produit;

public record ProduitResponse(
        Integer id,
        String reference,
        String designation,
        BigDecimal prix,
        Integer quantiteStock,
        Integer seuilAlerte,
        boolean stockFaible,
        String categorieId,
        String categorieNom) {

    public static ProduitResponse from(Produit p) {
        var c = p.getCategorie();
        return new ProduitResponse(
                p.getId(), p.getReference(), p.getDesignation(), p.getPrix(),
                p.getQuantiteStock(), p.getSeuilAlerte(), p.isStockFaible(),
                c != null ? c.getId() : null,
                c != null ? c.getNom() : null);
    }
}
