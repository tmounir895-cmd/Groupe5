package com.logistock.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.CategorieCreateRequest;
import com.logistock.dto.CategorieResponse;
import com.logistock.dto.CategorieUpdateRequest;
import com.logistock.entity.Categorie;
import com.logistock.exception.ConflictException;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.CategorieRepository;
import com.logistock.repository.ProduitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public List<CategorieResponse> lister() {
        return categorieRepository.findAllByOrderByNomAsc().stream().map(CategorieResponse::from).toList();
    }

    public CategorieResponse obtenir(String id) {
        return CategorieResponse.from(trouver(id));
    }

    @Transactional
    public CategorieResponse creer(CategorieCreateRequest req) {
        String id = req.id().trim();
        String nom = req.nom().trim();
        if (categorieRepository.existsById(id)) {
            throw new ConflictException("Une catégorie avec le code « " + id + " » existe déjà.");
        }
        if (categorieRepository.existsByNomIgnoreCase(nom)) {
            throw new ConflictException("Une catégorie nommée « " + nom + " » existe déjà.");
        }
        Categorie c = Categorie.builder().id(id).nom(nom).description(req.description()).build();
        return CategorieResponse.from(categorieRepository.save(c));
    }

    @Transactional
    public CategorieResponse modifier(String id, CategorieUpdateRequest req) {
        Categorie c = trouver(id);
        String nom = req.nom().trim();
        if (categorieRepository.existsByNomIgnoreCaseAndIdNot(nom, id)) {
            throw new ConflictException("Une catégorie nommée « " + nom + " » existe déjà.");
        }
        c.setNom(nom);
        c.setDescription(req.description());
        return CategorieResponse.from(c);
    }

    @Transactional
    public void supprimer(String id) {
        Categorie c = trouver(id);
        if (produitRepository.existsByCategorieId(id)) {
            throw new ConflictException("Impossible de supprimer la catégorie « " + id
                    + " » : des produits y sont rattachés.");
        }
        categorieRepository.delete(c);
    }

    private Categorie trouver(String id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable : " + id));
    }
}
