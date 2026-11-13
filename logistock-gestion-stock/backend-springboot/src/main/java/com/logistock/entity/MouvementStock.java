package com.logistock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Table `mouvement-stock` : trace de chaque entrée (ENTREE) ou sortie (SORTIE) de stock. */
@Entity
@Table(name = "`mouvement-stock`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStock {

    public static final String ENTREE = "ENTREE";
    public static final String SORTIE = "SORTIE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idmouvement-stock`")
    private Integer id;

    @Column(name = "`type-mouvement`", length = 10, nullable = false)
    private String type;

    @Column(name = "`quantité`")
    private Integer quantite;

    @Column(name = "`date-mouvement`")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilisateur")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproduit")
    private Produit produit;
}
