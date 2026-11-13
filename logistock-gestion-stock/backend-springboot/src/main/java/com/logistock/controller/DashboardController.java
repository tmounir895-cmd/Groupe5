package com.logistock.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistock.dto.DashboardResponse;
import com.logistock.service.DashboardService;

import lombok.RequiredArgsConstructor;

/** Tableau de bord : responsable de stock (et administrateur). */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','RESPONSABLE_STOCK')")
    public DashboardResponse tableauDeBord() {
        return service.tableauDeBord();
    }
}
