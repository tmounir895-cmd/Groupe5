package com.logistock.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Table `vente`. Le client est facultatif (vente au comptoir sans client enregistré). */
@Entity
@Table(name = "vente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idvente")
    private Integer id;

    @Column(name = "`date-vente`")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilisateur")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idclient")
    private Client client;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneVente> lignes = new ArrayList<>();

    public BigDecimal total() {
        return lignes.stream().map(LigneVente::sousTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
