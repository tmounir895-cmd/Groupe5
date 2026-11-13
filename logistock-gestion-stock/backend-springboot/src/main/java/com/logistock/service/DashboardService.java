package com.logistock.service;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.DashboardResponse;
import com.logistock.dto.MouvementResponse;
import com.logistock.dto.ProduitResponse;
import com.logistock.repository.MouvementStockRepository;
import com.logistock.repository.ProduitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final int NB_MOUVEMENTS = 10;

    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementRepository;

    /** Calculé à chaque appel à partir de la base : l'état du stock est toujours à jour. */
    public DashboardResponse tableauDeBord() {
        BigDecimal valeur = produitRepository.valeurStock();
        return new DashboardResponse(
                produitRepository.count(),
                produitRepository.compterStockFaible(),
                produitRepository.compterRuptures(),
                valeur == null ? BigDecimal.ZERO : valeur,
                produitRepository.findStockFaible().stream().map(ProduitResponse::from).toList(),
                mouvementRepository.recents(null, PageRequest.of(0, NB_MOUVEMENTS)).stream()
                        .map(MouvementResponse::from).toList());
    }
}
