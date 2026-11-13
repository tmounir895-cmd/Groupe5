package com.logistock.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.LoginRequest;
import com.logistock.dto.LoginResponse;
import com.logistock.dto.UtilisateurResponse;
import com.logistock.service.AuthService;
import com.logistock.service.UtilisateurService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    /** Public : renvoie un jeton JWT si l'email et le mot de passe sont corrects. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    /** Profil de l'utilisateur connecté (utile au front pour afficher le nom et choisir les menus). */
    @GetMapping("/me")
    public UtilisateurResponse me(@AuthenticationPrincipal Jwt jwt) {
        Number uid = jwt.getClaim("uid");
        return utilisateurService.obtenir(uid.intValue());
    }
}
