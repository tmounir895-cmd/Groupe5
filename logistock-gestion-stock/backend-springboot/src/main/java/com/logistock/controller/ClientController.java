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

import com.logistock.dto.ClientRequest;
import com.logistock.dto.ClientResponse;
import com.logistock.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Consulter, créer, modifier : administrateur et vendeur. Supprimer : administrateur. */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private static final String GERER = "hasAnyRole('ADMINISTRATEUR','VENDEUR')";

    private final ClientService service;

    @GetMapping
    @PreAuthorize(GERER)
    public List<ClientResponse> lister(@RequestParam(required = false) String q) {
        return service.lister(q);
    }

    @GetMapping("/{id}")
    @PreAuthorize(GERER)
    public ClientResponse obtenir(@PathVariable String id) {
        return service.obtenir(id);
    }

    @PostMapping
    @PreAuthorize(GERER)
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse creer(@Valid @RequestBody ClientRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize(GERER)
    public ClientResponse modifier(@PathVariable String id, @Valid @RequestBody ClientRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String id) {
        service.supprimer(id);
    }
}
