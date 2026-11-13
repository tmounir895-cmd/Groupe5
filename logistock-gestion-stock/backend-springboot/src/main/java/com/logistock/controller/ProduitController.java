package com.logistock.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.ProduitCreateRequest;
import com.logistock.dto.ProduitResponse;
import com.logistock.dto.ProduitUpdateRequest;
import com.logistock.service.ProduitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Lecture (liste, détail, alertes) : tout utilisateur connecté.
 * Écriture : administrateur et gestionnaire de stock.
 */
@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private static final String ECRITURE = "hasAnyRole('ADMINISTRATEUR','GESTIONNAIRE_STOCK')";

    private final ProduitService service;

    @GetMapping
    public List<ProduitResponse> lister(@RequestParam(required = false) String categorieId,
                                        @RequestParam(required = false) String q) {
        return service.lister(categorieId, q);
    }

    /** Produits dont le stock a atteint le seuil d'alerte (pour le tableau de bord). */
    @GetMapping("/alertes-stock")
    public List<ProduitResponse> alertesStock() {
        return service.alertesStock();
    }

    @GetMapping("/{id}")
    public ProduitResponse obtenir(@PathVariable Integer id) {
        return service.obtenir(id);
    }

    @PostMapping
    @PreAuthorize(ECRITURE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProduitResponse creer(@Valid @RequestBody ProduitCreateRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ECRITURE)
    public ProduitResponse modifier(@PathVariable Integer id, @Valid @RequestBody ProduitUpdateRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ECRITURE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        service.supprimer(id);
    }
}
