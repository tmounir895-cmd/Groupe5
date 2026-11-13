package com.logistock.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.VenteRequest;
import com.logistock.dto.VenteResponse;
import com.logistock.service.VenteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Enregistrer une vente : vendeur (et administrateur).
 * Consulter : vendeur (ses ventes), administrateur et responsable de stock (toutes).
 */
@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','VENDEUR')")
    @ResponseStatus(HttpStatus.CREATED)
    public VenteResponse enregistrer(@Valid @RequestBody VenteRequest req, @AuthenticationPrincipal Jwt jwt) {
        return service.enregistrer(req, idConnecte(jwt));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','VENDEUR','RESPONSABLE_STOCK')")
    public List<VenteResponse> lister(@AuthenticationPrincipal Jwt jwt) {
        return service.lister(idConnecte(jwt), voirTout(jwt));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','VENDEUR','RESPONSABLE_STOCK')")
    public VenteResponse obtenir(@PathVariable Integer id, @AuthenticationPrincipal Jwt jwt) {
        return service.obtenir(id, idConnecte(jwt), voirTout(jwt));
    }

    static Integer idConnecte(Jwt jwt) {
        Number uid = jwt.getClaim("uid");
        return uid.intValue();
    }

    private static boolean voirTout(Jwt jwt) {
        String role = jwt.getClaimAsString("role");
        return "ADMINISTRATEUR".equals(role) || "RESPONSABLE_STOCK".equals(role);
    }
}
