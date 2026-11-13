package com.logistock.dto;

import com.logistock.security.RoleNom;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UtilisateurCreateRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
        String nom,

        @Size(max = 45, message = "Le prénom ne doit pas dépasser 45 caractères")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
        String email,

        // 72 = limite de BCrypt (au-delà, le reste du mot de passe serait ignoré)
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, max = 72, message = "Le mot de passe doit faire entre 8 et 72 caractères")
        String motDePasse,

        @NotNull(message = "Le rôle est obligatoire")
        RoleNom role) {
}
