package com.logistock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Table `categorie`. L'identifiant est un code metier (varchar 5) fourni par l'utilisateur. */
@Entity
@Table(name = "categorie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    @Id
    @Column(name = "idcategorie", length = 5)
    private String id;

    // Les backticks disent a Hibernate de proteger le nom (tiret dans le nom de colonne).
    @Column(name = "`nom-categorie`", length = 45, unique = true)
    private String nom;

    @Column(name = "description", length = 255)
    private String description;
}
