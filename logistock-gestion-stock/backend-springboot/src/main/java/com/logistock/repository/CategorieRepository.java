package com.logistock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistock.entity.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie, String> {

    List<Categorie> findAllByOrderByNomAsc();

    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, String id);
}
