package com.logistock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistock.dto.ProduitCreateRequest;
import com.logistock.dto.ProduitResponse;
import com.logistock.entity.Categorie;
import com.logistock.entity.Produit;
import com.logistock.exception.ConflictException;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.CategorieRepository;
import com.logistock.repository.ProduitRepository;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock ProduitRepository produitRepository;
    @Mock CategorieRepository categorieRepository;
    @InjectMocks ProduitService service;

    private ProduitCreateRequest requete(int stock, int seuil) {
        return new ProduitCreateRequest("REF1", "Clou", new BigDecimal("100"), stock, seuil, "C1");
    }

    @Test
    void creer_refuse_une_reference_deja_utilisee() {
        when(produitRepository.existsByReferenceIgnoreCase("REF1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(requete(10, 5)));
        verify(produitRepository, never()).save(any());
    }

    @Test
    void creer_refuse_une_categorie_inconnue() {
        when(produitRepository.existsByReferenceIgnoreCase("REF1")).thenReturn(false);
        when(categorieRepository.findById("C1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.creer(requete(10, 5)));
        verify(produitRepository, never()).save(any());
    }

    @Test
    void creer_enregistre_le_produit_et_signale_le_stock_faible_au_seuil() {
        when(produitRepository.existsByReferenceIgnoreCase("REF1")).thenReturn(false);
        when(categorieRepository.findById("C1"))
                .thenReturn(Optional.of(Categorie.builder().id("C1").nom("Quincaillerie").build()));
        when(produitRepository.save(any(Produit.class))).thenAnswer(i -> i.getArgument(0));

        ProduitResponse r = service.creer(requete(5, 5));

        assertEquals("REF1", r.reference());
        assertEquals("C1", r.categorieId());
        assertTrue(r.stockFaible(), "stock = seuil => alerte");
    }

    @Test
    void stock_faible_seulement_si_quantite_inferieure_ou_egale_au_seuil() {
        assertTrue(Produit.builder().quantiteStock(0).seuilAlerte(5).build().isStockFaible());
        assertTrue(Produit.builder().quantiteStock(5).seuilAlerte(5).build().isStockFaible());
        assertFalse(Produit.builder().quantiteStock(6).seuilAlerte(5).build().isStockFaible());
    }
}
