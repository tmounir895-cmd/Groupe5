package com.logistock.dto;

import com.logistock.entity.Utilisateur;

/** Jamais de mot de passe (ni hash) dans les réponses. */
public record UtilisateurResponse(Integer id, String nom, String prenom, String email, String role) {

    public static UtilisateurResponse from(Utilisateur u) {
        return new UtilisateurResponse(u.getId(), u.getNom(), u.getPrenom(), u.getEmail(),
                u.getRole() != null ? u.getRole().getNom() : null);
    }
}
