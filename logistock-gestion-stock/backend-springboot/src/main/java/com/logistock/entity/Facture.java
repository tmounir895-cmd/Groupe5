package com.logistock.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Table `facture` : une facture par vente. Le montant se calcule à partir des lignes de la vente. */
@Entity
@Table(name = "facture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfacture")
    private Integer id;

    @Column(name = "`numero-facture`", length = 20)
    private String numero;

    // Le SQL écrit `date-emession` (faute de frappe conservée).
    @Column(name = "`date-emession`")
    private LocalDate dateEmission;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvente")
    private Vente vente;
}
