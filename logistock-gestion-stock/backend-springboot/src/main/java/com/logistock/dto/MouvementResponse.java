package com.logistock.dto;

import java.time.LocalDateTime;

import com.logistock.entity.MouvementStock;

/** stockActuel = quantité en stock du produit au moment de la réponse. */
public record MouvementResponse(Integer id, String type, Integer quantite, LocalDateTime date,
                                Integer produitId, String designation, Integer utilisateurId, Integer stockActuel) {

    public static MouvementResponse from(MouvementStock m) {
        var u = m.getUtilisateur();
        return new MouvementResponse(m.getId(), m.getType(), m.getQuantite(), m.getDate(),
                m.getProduit().getId(), m.getProduit().getDesignation(),
                u == null ? null : u.getId(), m.getProduit().getQuantiteStock());
    }
}
