package com.logistock.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.PasswordResetRequest;
import com.logistock.dto.RoleUpdateRequest;
import com.logistock.dto.UtilisateurCreateRequest;
import com.logistock.dto.UtilisateurResponse;
import com.logistock.service.UtilisateurService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestion des comptes : réservée à l'administrateur. */
@RestController
@RequestMapping("/api/utilisateurs")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;

    @GetMapping
    public List<UtilisateurResponse> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public UtilisateurResponse obtenir(@PathVariable Integer id) {
        return service.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse creer(@Valid @RequestBody UtilisateurCreateRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}/role")
    public UtilisateurResponse modifierRole(@PathVariable Integer id,
                                            @Valid @RequestBody RoleUpdateRequest req,
                                            @AuthenticationPrincipal Jwt jwt) {
        return service.modifierRole(id, req, idConnecte(jwt));
    }

    @PutMapping("/{id}/mot-de-passe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reinitialiserMotDePasse(@PathVariable Integer id, @Valid @RequestBody PasswordResetRequest req) {
        service.reinitialiserMotDePasse(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id, @AuthenticationPrincipal Jwt jwt) {
        service.supprimer(id, idConnecte(jwt));
    }

    private static Integer idConnecte(Jwt jwt) {
        Number uid = jwt.getClaim("uid");
        return uid.intValue();
    }
}
