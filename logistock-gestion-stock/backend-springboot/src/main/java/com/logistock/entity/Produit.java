package com.logistock.entity;

import java.math.BigDecimal;

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

/** Table `produit`. */
@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT (voir le script de migration)
    @Column(name = "idproduit")
    private Integer id;

    @Column(name = "reference", length = 45, nullable = false)
    private String reference;

    @Column(name = "designation", length = 40, nullable = false)
    private String designation;

    @Column(name = "prix", nullable = false, precision = 10, scale = 0)
    private BigDecimal prix;

    @Column(name = "`quantité-stock`", nullable = false)
    private Integer quantiteStock;

    // La colonne s'appelle `seul-alerte` dans le SQL (faute de frappe conservee pour ne rien casser).
    @Column(name = "`seul-alerte`", nullable = false)
    private Integer seuilAlerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie")
    private Categorie categorie;

    /** Regle metier : alerte des que le stock atteint (ou passe sous) le seuil. */
    public boolean isStockFaible() {
        return quantiteStock != null && seuilAlerte != null && quantiteStock <= seuilAlerte;
    }
}
