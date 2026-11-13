package com.logistock.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.ChiffreAffairesResponse;
import com.logistock.service.VenteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final VenteService venteService;

    /** Montant total des factures sur une période (dates au format 2026-11-08). Par défaut : le mois en cours. */
    @GetMapping("/chiffre-affaires")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','VENDEUR','RESPONSABLE_STOCK')")
    public ChiffreAffairesResponse chiffreAffaires(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return venteService.chiffreAffaires(debut, fin);
    }
}
