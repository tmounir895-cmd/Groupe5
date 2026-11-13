package com.logistock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EntreeStockRequest(
        @NotNull(message = "Le produit est obligatoire") Integer produitId,
        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être supérieure à 0")
        Integer quantite) {
}
