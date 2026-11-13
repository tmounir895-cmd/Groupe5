package com.logistock.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.EntreeStockRequest;
import com.logistock.dto.MouvementResponse;
import com.logistock.entity.MouvementStock;
import com.logistock.entity.Produit;
import com.logistock.entity.Utilisateur;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.MouvementStockRepository;
import com.logistock.repository.ProduitRepository;
import com.logistock.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private static final int MAX_MOUVEMENTS = 200;

    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockRepository mouvementRepository;

    /** Entrée de marchandises : le stock augmente et un mouvement ENTREE est enregistré. */
    @Transactional
    public MouvementResponse enregistrerEntree(EntreeStockRequest req, Integer idUtilisateur) {
        Produit produit = produitRepository.findByIdForUpdate(req.produitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + req.produitId()));
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + idUtilisateur));

        produit.setQuantiteStock(produit.getQuantiteStock() + req.quantite());

        MouvementStock mouvement = MouvementStock.builder()
                .type(MouvementStock.ENTREE)
                .quantite(req.quantite())
                .date(LocalDateTime.now())
                .utilisateur(utilisateur)
                .produit(produit)
                .build();
        return MouvementResponse.from(mouvementRepository.save(mouvement));
    }

    /** Les 200 mouvements les plus récents, éventuellement pour un seul produit. */
    public List<MouvementResponse> mouvements(Integer produitId) {
        return mouvementRepository.recents(produitId, PageRequest.of(0, MAX_MOUVEMENTS)).stream()
                .map(MouvementResponse::from).toList();
    }
}
