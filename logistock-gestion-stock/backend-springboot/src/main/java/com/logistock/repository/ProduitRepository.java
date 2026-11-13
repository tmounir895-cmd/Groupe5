package com.logistock.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logistock.entity.Produit;

import jakarta.persistence.LockModeType;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    boolean existsByReferenceIgnoreCase(String reference);

    boolean existsByReferenceIgnoreCaseAndIdNot(String reference, Integer id);

    boolean existsByCategorieId(String categorieId);

    /** Liste filtrable par categorie et/ou texte (designation ou reference). Parametres nuls = pas de filtre. */
    @Query("""
        select p from Produit p left join fetch p.categorie
        where (:categorieId is null or p.categorie.id = :categorieId)
          and (:q is null
               or lower(p.designation) like lower(concat('%', :q, '%'))
               or lower(p.reference) like lower(concat('%', :q, '%')))
        order by p.designation asc
        """)
    List<Produit> rechercher(@Param("categorieId") String categorieId, @Param("q") String q);

    /** Produits dont le stock a atteint le seuil d'alerte, les plus critiques d'abord. */
    @Query("""
        select p from Produit p left join fetch p.categorie
        where p.quantiteStock <= p.seuilAlerte
        order by p.quantiteStock asc, p.designation asc
        """)
    List<Produit> findStockFaible();

    /**
     * Verrouille les produits (SELECT ... FOR UPDATE) pendant la transaction : deux ventes simultanées
     * du même produit ne peuvent plus lire le même stock. Tri par id pour toujours verrouiller dans le même ordre.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produit p where p.id in :ids order by p.id")
    List<Produit> findAllByIdForUpdate(@Param("ids") Collection<Integer> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produit p where p.id = :id")
    Optional<Produit> findByIdForUpdate(@Param("id") Integer id);

    /** Nombre de produits dont le stock a atteint le seuil d'alerte (rupture comprise). */
    @Query("select count(p) from Produit p where p.quantiteStock <= p.seuilAlerte")
    long compterStockFaible();

    @Query("select count(p) from Produit p where p.quantiteStock = 0")
    long compterRuptures();

    /** Valeur du stock au prix de vente (somme prix x quantité). Renvoie null s'il n'y a aucun produit. */
    @Query("select sum(p.prix * p.quantiteStock) from Produit p")
    BigDecimal valeurStock();
}
