package com.logistock.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.ProduitCreateRequest;
import com.logistock.dto.ProduitResponse;
import com.logistock.dto.ProduitUpdateRequest;
import com.logistock.entity.Categorie;
import com.logistock.entity.Produit;
import com.logistock.exception.ConflictException;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.CategorieRepository;
import com.logistock.repository.ProduitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;

    public List<ProduitResponse> lister(String categorieId, String recherche) {
        return produitRepository.rechercher(videEnNull(categorieId), videEnNull(recherche)).stream()
                .map(ProduitResponse::from).toList();
    }

    public ProduitResponse obtenir(Integer id) {
        return ProduitResponse.from(trouver(id));
    }

    /** Produits dont le stock a atteint le seuil d'alerte. */
    public List<ProduitResponse> alertesStock() {
        return produitRepository.findStockFaible().stream().map(ProduitResponse::from).toList();
    }

    @Transactional
    public ProduitResponse creer(ProduitCreateRequest req) {
        String reference = req.reference().trim();
        if (produitRepository.existsByReferenceIgnoreCase(reference)) {
            throw new ConflictException("Un produit avec la référence « " + reference + " » existe déjà.");
        }
        Categorie categorie = trouverCategorie(req.categorieId());
        Produit p = Produit.builder()
                .reference(reference)
                .designation(req.designation().trim())
                .prix(req.prix())
                .quantiteStock(req.quantiteStock())
                .seuilAlerte(req.seuilAlerte())
                .categorie(categorie)
                .build();
        return ProduitResponse.from(produitRepository.save(p));
    }

    @Transactional
    public ProduitResponse modifier(Integer id, ProduitUpdateRequest req) {
        Produit p = trouver(id);
        String reference = req.reference().trim();
        if (produitRepository.existsByReferenceIgnoreCaseAndIdNot(reference, id)) {
            throw new ConflictException("Un produit avec la référence « " + reference + " » existe déjà.");
        }
        p.setReference(reference);
        p.setDesignation(req.designation().trim());
        p.setPrix(req.prix());
        p.setSeuilAlerte(req.seuilAlerte());
        p.setCategorie(trouverCategorie(req.categorieId()));
        return ProduitResponse.from(p);
    }

    @Transactional
    public void supprimer(Integer id) {
        Produit p = trouver(id);
        produitRepository.delete(p);
        // flush : si le produit est déjà utilisé (vente, mouvement), MySQL refuse ici, pas plus tard.
        produitRepository.flush();
    }

    private Produit trouver(Integer id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + id));
    }

    private Categorie trouverCategorie(String id) {
        return categorieRepository.findById(id.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable : " + id));
    }

    private static String videEnNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
