package com.logistock.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.EntreeStockRequest;
import com.logistock.dto.MouvementResponse;
import com.logistock.service.StockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Entrées de marchandises : gestionnaire de stock (et administrateur). Historique : + responsable de stock. */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @PostMapping("/entrees")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','GESTIONNAIRE_STOCK')")
    @ResponseStatus(HttpStatus.CREATED)
    public MouvementResponse enregistrerEntree(@Valid @RequestBody EntreeStockRequest req,
                                               @AuthenticationPrincipal Jwt jwt) {
        return service.enregistrerEntree(req, VenteController.idConnecte(jwt));
    }

    @GetMapping("/mouvements")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','GESTIONNAIRE_STOCK','RESPONSABLE_STOCK')")
    public List<MouvementResponse> mouvements(@RequestParam(required = false) Integer produitId) {
        return service.mouvements(produitId);
    }
}
