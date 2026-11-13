package com.logistock.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistock.entity.Facture;

public interface FactureRepository extends JpaRepository<Facture, Integer> {

    List<Facture> findByVenteIdIn(Collection<Integer> idsVentes);
}
