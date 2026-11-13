package com.logistock.dto;

import java.math.BigDecimal;

import com.logistock.entity.LigneVente;

public record LigneVenteResponse(Integer produitId, String reference, String designation,
                                 Integer quantite, BigDecimal prixUnitaire, BigDecimal sousTotal) {

    public static LigneVenteResponse from(LigneVente l) {
        return new LigneVenteResponse(l.getProduit().getId(), l.getProduit().getReference(),
                l.getProduit().getDesignation(), l.getQuantite(), l.getPrixUnitaire(), l.sousTotal());
    }
}
