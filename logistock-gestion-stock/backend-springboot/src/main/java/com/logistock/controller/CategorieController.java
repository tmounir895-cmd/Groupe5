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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.CategorieCreateRequest;
import com.logistock.dto.CategorieResponse;
import com.logistock.dto.CategorieUpdateRequest;
import com.logistock.service.CategorieService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Lecture : tout utilisateur connecté (le vendeur a besoin du catalogue).
 * Écriture : administrateur et gestionnaire de stock.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private static final String ECRITURE = "hasAnyRole('ADMINISTRATEUR','GESTIONNAIRE_STOCK')";

    private final CategorieService service;

    @GetMapping
    public List<CategorieResponse> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public CategorieResponse obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @PostMapping
    @PreAuthorize(ECRITURE)
    @ResponseStatus(HttpStatus.CREATED)
    public CategorieResponse creer(@Valid @RequestBody CategorieCreateRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ECRITURE)
    public CategorieResponse modifier(@PathVariable String id, @Valid @RequestBody CategorieUpdateRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ECRITURE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String id) {
        service.supprimer(id);
    }
}
