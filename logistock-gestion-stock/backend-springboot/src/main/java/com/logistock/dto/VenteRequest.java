package com.logistock.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/** clientId est facultatif (null = vente sans client enregistré). */
public record VenteRequest(
        String clientId,

        @NotEmpty(message = "Une vente doit contenir au moins une ligne")
        List<@Valid LigneVenteRequest> lignes) {
}
