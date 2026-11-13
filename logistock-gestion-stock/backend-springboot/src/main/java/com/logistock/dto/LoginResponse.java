package com.logistock.dto;

/** Réponse de connexion : le front envoie ensuite "Authorization: Bearer <token>". */
public record LoginResponse(String token, String type, long expiresInSeconds, UtilisateurResponse utilisateur) {
}
