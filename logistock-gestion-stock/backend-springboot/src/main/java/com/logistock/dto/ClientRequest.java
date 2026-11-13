package com.logistock.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
        String nom,

        @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(regexp = "[0-9+\\s]{6,20}", message = "Téléphone invalide (chiffres, espaces et + uniquement, 6 à 20 caractères)")
        String telephone,

        @Email(message = "Email invalide")
        @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
        String email,

        @NotBlank(message = "L'adresse est obligatoire")
        @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
        String adresse) {
}
