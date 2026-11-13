package com.logistock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.logistock.entity.Vente;

/** alertesStockFaible : produits de cette vente dont le stock a atteint le seuil d'alerte (vide si aucun). */
public record VenteResponse(Integer id, LocalDateTime date, String vendeur, String clientId, String clientNom,
                            String factureNumero, List<LigneVenteResponse> lignes, BigDecimal total,
                            List<String> alertesStockFaible) {

    public static VenteResponse from(Vente v, String factureNumero, List<String> alertes) {
        var u = v.getUtilisateur();
        String vendeur = u == null ? null
                : ((u.getPrenom() == null ? "" : u.getPrenom() + " ") + u.getNom()).trim();
        var c = v.getClient();
        return new VenteResponse(v.getId(), v.getDate(), vendeur,
                c == null ? null : c.getId(), c == null ? null : c.getNom(),
                factureNumero, v.getLignes().stream().map(LigneVenteResponse::from).toList(), v.total(), alertes);
    }
}
