package com.logistock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategorieCreateRequest(
        @NotBlank(message = "Le code de la catégorie est obligatoire")
        @Size(max = 5, message = "Le code ne doit pas dépasser 5 caractères")
        @Pattern(regexp = "[A-Za-z0-9]*", message = "Le code ne doit contenir que des lettres et des chiffres")
        String id,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
        String nom,

        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
        String description) {
}
