package com.logistock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistock.dto.EntreeStockRequest;
import com.logistock.dto.MouvementResponse;
import com.logistock.entity.MouvementStock;
import com.logistock.entity.Produit;
import com.logistock.entity.Utilisateur;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.MouvementStockRepository;
import com.logistock.repository.ProduitRepository;
import com.logistock.repository.UtilisateurRepository;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock ProduitRepository produitRepository;
    @Mock UtilisateurRepository utilisateurRepository;
    @Mock MouvementStockRepository mouvementRepository;
    @InjectMocks StockService service;

    @Test
    void entree_augmente_le_stock_et_trace_un_mouvement_ENTREE() {
        Produit p = Produit.builder().id(1).designation("Clou").quantiteStock(4).seuilAlerte(5).build();
        when(produitRepository.findByIdForUpdate(1)).thenReturn(Optional.of(p));
        when(utilisateurRepository.findById(9)).thenReturn(Optional.of(Utilisateur.builder().id(9).nom("Kone").build()));
        when(mouvementRepository.save(any(MouvementStock.class))).thenAnswer(i -> i.getArgument(0));

        MouvementResponse r = service.enregistrerEntree(new EntreeStockRequest(1, 6), 9);

        assertEquals(10, p.getQuantiteStock());
        assertEquals("ENTREE", r.type());
        assertEquals(6, r.quantite());
    }

    @Test
    void entree_refusee_pour_un_produit_inconnu() {
        when(produitRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.enregistrerEntree(new EntreeStockRequest(99, 1), 9));
        verify(mouvementRepository, never()).save(any());
    }
}
