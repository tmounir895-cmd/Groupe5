package com.logistock.dto;

import com.logistock.entity.Categorie;

public record CategorieResponse(String id, String nom, String description) {

    public static CategorieResponse from(Categorie c) {
        return new CategorieResponse(c.getId(), c.getNom(), c.getDescription());
    }
}
