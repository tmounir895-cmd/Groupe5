package com.logistock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategorieUpdateRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
        String nom,

        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
        String description) {
}
