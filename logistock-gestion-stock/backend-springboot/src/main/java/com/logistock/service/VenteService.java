package com.logistock.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.ChiffreAffairesResponse;
import com.logistock.dto.LigneVenteRequest;
import com.logistock.dto.VenteRequest;
import com.logistock.dto.VenteResponse;
import com.logistock.entity.Client;
import com.logistock.entity.Facture;
import com.logistock.entity.LigneVente;
import com.logistock.entity.MouvementStock;
import com.logistock.entity.Produit;
import com.logistock.entity.Utilisateur;
import com.logistock.entity.Vente;
import com.logistock.exception.ConflictException;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.ClientRepository;
import com.logistock.repository.FactureRepository;
import com.logistock.repository.MouvementStockRepository;
import com.logistock.repository.ProduitRepository;
import com.logistock.repository.UtilisateurRepository;
import com.logistock.repository.VenteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenteService {

    private final VenteRepository venteRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockRepository mouvementRepository;

    /**
     * Enregistre une vente. Tout se passe dans UNE transaction : si une ligne échoue,
     * rien n'est enregistré et le stock reste intact.
     * - la vente est refusée si le stock d'un produit est insuffisant ;
     * - sinon le stock baisse, un mouvement SORTIE est tracé par ligne et une facture est créée.
     */
    @Transactional
    public VenteResponse enregistrer(VenteRequest req, Integer idVendeur) {
        // 0. Client facultatif : on vérifie qu'il existe avant de verrouiller quoi que ce soit
        Client client = null;
        if (req.clientId() != null && !req.clientId().isBlank()) {
            client = clientRepository.findById(req.clientId().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Client introuvable : " + req.clientId()));
        }

        // 1. Un même produit sur deux lignes : on additionne (TreeMap = ordre des ids, donc verrous toujours dans le même ordre)
        Map<Integer, Integer> quantites = new TreeMap<>();
        for (LigneVenteRequest l : req.lignes()) {
            quantites.merge(l.produitId(), l.quantite(), Integer::sum);
        }

        // 2. Verrouillage des produits : une autre vente simultanée doit attendre la fin de celle-ci
        Map<Integer, Produit> produits = produitRepository.findAllByIdForUpdate(quantites.keySet()).stream()
                .collect(Collectors.toMap(Produit::getId, Function.identity()));

        // 3. Produits inconnus et stock insuffisant : on vérifie TOUT avant de modifier quoi que ce soit
        List<String> manques = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : quantites.entrySet()) {
            Produit p = produits.get(e.getKey());
            if (p == null) {
                throw new ResourceNotFoundException("Produit introuvable : " + e.getKey());
            }
            if (p.getQuantiteStock() < e.getValue()) {
                manques.add("« " + p.getDesignation() + " » : demandé " + e.getValue()
                        + ", disponible " + p.getQuantiteStock());
            }
        }
        if (!manques.isEmpty()) {
            throw new ConflictException("Stock insuffisant. " + String.join(" ; ", manques));
        }

        // 4. Vente, lignes (prix figé), baisse du stock et mouvements SORTIE
        Utilisateur vendeur = utilisateurRepository.findById(idVendeur)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + idVendeur));
        LocalDateTime maintenant = LocalDateTime.now();
        Vente vente = Vente.builder().date(maintenant).utilisateur(vendeur).client(client).build();
        List<MouvementStock> mouvements = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : quantites.entrySet()) {
            Produit p = produits.get(e.getKey());
            p.setQuantiteStock(p.getQuantiteStock() - e.getValue());
            vente.getLignes().add(LigneVente.builder()
                    .vente(vente).produit(p).quantite(e.getValue()).prixUnitaire(p.getPrix()).build());
            mouvements.add(MouvementStock.builder()
                    .type(MouvementStock.SORTIE).quantite(e.getValue()).date(maintenant)
                    .utilisateur(vendeur).produit(p).build());
        }
        venteRepository.saveAndFlush(vente); // pour obtenir l'identifiant de la vente

        // 5. Facture (numéro basé sur l'id de la vente : toujours unique) et traçabilité
        Facture facture = factureRepository.save(Facture.builder()
                .vente(vente)
                .dateEmission(LocalDate.now())
                .numero(String.format("F%d-%06d", maintenant.getYear(), vente.getId()))
                .build());
        mouvementRepository.saveAll(mouvements);

        // 6. Alerte : produits vendus dont le stock a atteint le seuil
        List<String> alertes = produits.values().stream()
                .filter(Produit::isStockFaible)
                .map(p -> p.getDesignation() + " (stock : " + p.getQuantiteStock()
                        + ", seuil : " + p.getSeuilAlerte() + ")")
                .toList();
        return VenteResponse.from(vente, facture.getNumero(), alertes);
    }

    /** voirTout = administrateur / responsable de stock. Le vendeur ne voit que ses propres ventes. */
    public List<VenteResponse> lister(Integer idUtilisateur, boolean voirTout) {
        List<Vente> ventes = venteRepository.lister(voirTout ? null : idUtilisateur);
        return versReponses(ventes);
    }

    public VenteResponse obtenir(Integer id, Integer idUtilisateur, boolean voirTout) {
        Vente vente = venteRepository.trouverDetail(id)
                .filter(v -> voirTout || (v.getUtilisateur() != null && v.getUtilisateur().getId().equals(idUtilisateur)))
                .orElseThrow(() -> new ResourceNotFoundException("Vente introuvable : " + id));
        return versReponses(List.of(vente)).get(0);
    }

    /** Montant total des ventes (somme des factures) entre deux dates incluses. Par défaut : le mois en cours. */
    public ChiffreAffairesResponse chiffreAffaires(LocalDate debut, LocalDate fin) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate d = debut != null ? debut : aujourdhui.withDayOfMonth(1);
        LocalDate f = fin != null ? fin : aujourdhui;
        if (f.isBefore(d)) {
            throw new ConflictException("La date de fin doit être postérieure à la date de début.");
        }
        LocalDateTime de = d.atStartOfDay();
        LocalDateTime a = f.plusDays(1).atStartOfDay();
        BigDecimal total = venteRepository.chiffreAffaires(de, a);
        return new ChiffreAffairesResponse(d, f, venteRepository.compter(de, a),
                total == null ? BigDecimal.ZERO : total);
    }

    private List<VenteResponse> versReponses(List<Vente> ventes) {
        if (ventes.isEmpty()) {
            return List.of();
        }
        Map<Integer, String> numeros = factureRepository
                .findByVenteIdIn(ventes.stream().map(Vente::getId).toList()).stream()
                .collect(Collectors.toMap(f -> f.getVente().getId(), Facture::getNumero, (a, b) -> a));
        return ventes.stream()
                .map(v -> VenteResponse.from(v, numeros.get(v.getId()), List.of()))
                .toList();
    }
}
