package com.logistock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistock.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Utilisateur> findAllByOrderByNomAsc();
}
