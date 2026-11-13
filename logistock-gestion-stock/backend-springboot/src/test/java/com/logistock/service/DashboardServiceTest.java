package com.logistock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.logistock.dto.DashboardResponse;
import com.logistock.entity.Produit;
import com.logistock.repository.MouvementStockRepository;
import com.logistock.repository.ProduitRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock ProduitRepository produitRepository;
    @Mock MouvementStockRepository mouvementRepository;
    @InjectMocks DashboardService service;

    @Test
    void sans_produit_le_tableau_de_bord_renvoie_des_zeros() {
        when(produitRepository.valeurStock()).thenReturn(null);
        when(produitRepository.count()).thenReturn(0L);
        when(produitRepository.compterStockFaible()).thenReturn(0L);
        when(produitRepository.compterRuptures()).thenReturn(0L);
        when(produitRepository.findStockFaible()).thenReturn(List.of());
        when(mouvementRepository.recents(isNull(), any(Pageable.class))).thenReturn(List.of());

        DashboardResponse r = service.tableauDeBord();

        assertEquals(BigDecimal.ZERO, r.valeurStock());
        assertEquals(0, r.nombreProduits());
        assertTrue(r.produitsStockFaible().isEmpty());
    }

    @Test
    void les_produits_en_stock_faible_sont_listes_et_comptes() {
        Produit p = Produit.builder().id(1).reference("R1").designation("Clou")
                .prix(new BigDecimal("100")).quantiteStock(2).seuilAlerte(5).build();
        when(produitRepository.valeurStock()).thenReturn(new BigDecimal("200"));
        when(produitRepository.count()).thenReturn(1L);
        when(produitRepository.compterStockFaible()).thenReturn(1L);
        when(produitRepository.compterRuptures()).thenReturn(0L);
        when(produitRepository.findStockFaible()).thenReturn(List.of(p));
        when(mouvementRepository.recents(isNull(), any(Pageable.class))).thenReturn(List.of());

        DashboardResponse r = service.tableauDeBord();

        assertEquals(new BigDecimal("200"), r.valeurStock());
        assertEquals(1, r.nombreProduitsStockFaible());
        assertEquals("Clou", r.produitsStockFaible().get(0).designation());
        assertTrue(r.produitsStockFaible().get(0).stockFaible());
    }
}
