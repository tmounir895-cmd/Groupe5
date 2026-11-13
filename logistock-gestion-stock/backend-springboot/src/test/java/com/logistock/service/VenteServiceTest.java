package com.logistock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistock.dto.LigneVenteRequest;
import com.logistock.dto.VenteRequest;
import com.logistock.dto.VenteResponse;
import com.logistock.entity.Facture;
import com.logistock.entity.Produit;
import com.logistock.entity.Utilisateur;
import com.logistock.entity.Vente;
import com.logistock.exception.ConflictException;
import com.logistock.repository.ClientRepository;
import com.logistock.repository.FactureRepository;
import com.logistock.repository.MouvementStockRepository;
import com.logistock.repository.ProduitRepository;
import com.logistock.repository.UtilisateurRepository;
import com.logistock.repository.VenteRepository;

@ExtendWith(MockitoExtension.class)
class VenteServiceTest {

    @Mock VenteRepository venteRepository;
    @Mock FactureRepository factureRepository;
    @Mock ClientRepository clientRepository;
    @Mock ProduitRepository produitRepository;
    @Mock UtilisateurRepository utilisateurRepository;
    @Mock MouvementStockRepository mouvementRepository;
    @InjectMocks VenteService service;

    private Produit produit(int id, int stock, int seuil, String prix) {
        return Produit.builder().id(id).reference("REF" + id).designation("Produit " + id)
                .prix(new BigDecimal(prix)).quantiteStock(stock).seuilAlerte(seuil).build();
    }

    private VenteRequest requete(int produitId, int quantite) {
        return new VenteRequest(null, List.of(new LigneVenteRequest(produitId, quantite)));
    }

    @Test
    void vente_refusee_si_stock_insuffisant_et_stock_inchange() {
        Produit p = produit(1, 3, 1, "100");
        when(produitRepository.findAllByIdForUpdate(any())).thenReturn(List.of(p));

        ConflictException e = assertThrows(ConflictException.class, () -> service.enregistrer(requete(1, 5), 7));

        assertTrue(e.getMessage().contains("demandé 5, disponible 3"));
        assertEquals(3, p.getQuantiteStock());
        verify(venteRepository, never()).saveAndFlush(any());
    }

    @Test
    void vente_acceptee_baisse_le_stock_calcule_le_total_et_numerote_la_facture() {
        Produit p = produit(1, 10, 5, "200");
        when(produitRepository.findAllByIdForUpdate(any())).thenReturn(List.of(p));
        when(utilisateurRepository.findById(7)).thenReturn(Optional.of(Utilisateur.builder().id(7).nom("Diallo").build()));
        when(venteRepository.saveAndFlush(any(Vente.class))).thenAnswer(i -> {
            Vente v = i.getArgument(0);
            v.setId(12);
            return v;
        });
        when(factureRepository.save(any(Facture.class))).thenAnswer(i -> i.getArgument(0));

        VenteResponse r = service.enregistrer(requete(1, 5), 7);

        assertEquals(5, p.getQuantiteStock());
        assertEquals(new BigDecimal("1000"), r.total());
        assertTrue(r.factureNumero().endsWith("-000012"));
        assertEquals(1, r.alertesStockFaible().size(), "stock 5 = seuil 5 => alerte");
    }

    @Test
    void deux_lignes_du_meme_produit_sont_additionnees_avant_de_verifier_le_stock() {
        Produit p = produit(1, 5, 1, "100");
        when(produitRepository.findAllByIdForUpdate(any())).thenReturn(List.of(p));
        VenteRequest req = new VenteRequest(null, List.of(new LigneVenteRequest(1, 3), new LigneVenteRequest(1, 3)));

        assertThrows(ConflictException.class, () -> service.enregistrer(req, 7));
        assertEquals(5, p.getQuantiteStock());
    }

    @Test
    void vente_refusee_pour_un_client_inconnu_avant_de_toucher_au_stock() {
        when(clientRepository.findById("C9999")).thenReturn(Optional.empty());
        VenteRequest req = new VenteRequest("C9999", List.of(new LigneVenteRequest(1, 1)));

        assertThrows(com.logistock.exception.ResourceNotFoundException.class, () -> service.enregistrer(req, 7));
        verify(produitRepository, never()).findAllByIdForUpdate(any());
    }
}
