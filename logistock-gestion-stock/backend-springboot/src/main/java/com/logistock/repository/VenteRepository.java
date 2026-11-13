package com.logistock.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logistock.entity.Vente;

public interface VenteRepository extends JpaRepository<Vente, Integer> {

    /** Ventes avec leurs lignes, produits et vendeur. idVendeur nul = toutes les ventes. */
    @Query("""
        select distinct v from Vente v
        left join fetch v.lignes l
        left join fetch l.produit
        left join fetch v.utilisateur
        left join fetch v.client
        where (:idVendeur is null or v.utilisateur.id = :idVendeur)
        order by v.date desc
        """)
    List<Vente> lister(@Param("idVendeur") Integer idVendeur);

    @Query("""
        select v from Vente v
        left join fetch v.lignes l
        left join fetch l.produit
        left join fetch v.utilisateur
        left join fetch v.client
        where v.id = :id
        """)
    Optional<Vente> trouverDetail(@Param("id") Integer id);

    /** Chiffre d'affaires entre deux dates (debut inclus, fin exclue). Peut renvoyer null s'il n'y a aucune vente. */
    @Query("""
        select sum(l.quantite * l.prixUnitaire) from LigneVente l
        where l.vente.date >= :debut and l.vente.date < :fin
        """)
    BigDecimal chiffreAffaires(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("select count(v) from Vente v where v.date >= :debut and v.date < :fin")
    long compter(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
