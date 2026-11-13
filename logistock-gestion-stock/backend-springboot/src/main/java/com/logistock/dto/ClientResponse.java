package com.logistock.dto;

import com.logistock.entity.Client;

public record ClientResponse(String id, String nom, String telephone, String email, String adresse) {

    public static ClientResponse from(Client c) {
        return new ClientResponse(c.getId(), c.getNom(), c.getTelephone(), c.getEmail(), c.getAdresse());
    }
}
