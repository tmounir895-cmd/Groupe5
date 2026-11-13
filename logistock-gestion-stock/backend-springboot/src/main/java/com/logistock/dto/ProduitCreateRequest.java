package com.logistock.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Création : le stock initial est saisi ici ; ensuite il ne bouge que par entrées/ventes. */
public record ProduitCreateRequest(
        @NotBlank(message = "La référence est obligatoire")
        @Size(max = 45, message = "La référence ne doit pas dépasser 45 caractères")
        String reference,

        @NotBlank(message = "La désignation est obligatoire")
        @Size(max = 40, message = "La désignation ne doit pas dépasser 40 caractères")
        String designation,

        @NotNull(message = "Le prix est obligatoire")
        @PositiveOrZero(message = "Le prix ne peut pas être négatif")
        @Digits(integer = 10, fraction = 0, message = "Le prix doit être un nombre entier (10 chiffres max)")
        BigDecimal prix,

        @NotNull(message = "La quantité en stock est obligatoire")
        @PositiveOrZero(message = "La quantité ne peut pas être négative")
        Integer quantiteStock,

        @NotNull(message = "Le seuil d'alerte est obligatoire")
        @PositiveOrZero(message = "Le seuil d'alerte ne peut pas être négatif")
        Integer seuilAlerte,

        @NotBlank(message = "La catégorie est obligatoire")
        @Size(max = 5, message = "Code catégorie invalide")
        String categorieId) {
}
