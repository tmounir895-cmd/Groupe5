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

/** Table `ligne-vente`. Le prix unitaire est figé au moment de la vente (le prix du produit pourra changer ensuite). */
@Entity
@Table(name = "`ligne-vente`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idligne-vente`")
    private Integer id;

    @Column(name = "`quantité`")
    private Integer quantite;

    @Column(name = "`prix-unitaire`", precision = 10, scale = 0)
    private BigDecimal prixUnitaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvente")
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproduit")
    private Produit produit;

    public BigDecimal sousTotal() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
}
