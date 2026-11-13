package com.logistock.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logistock.entity.MouvementStock;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {

    /** Mouvements les plus récents d'abord. produitId nul = tous les produits. */
    @Query("""
        select m from MouvementStock m
        join fetch m.produit
        left join fetch m.utilisateur
        where (:produitId is null or m.produit.id = :produitId)
        order by m.date desc, m.id desc
        """)
    List<MouvementStock> recents(@Param("produitId") Integer produitId, Pageable pageable);
}
