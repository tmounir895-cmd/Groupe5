package com.logistock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistock.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByNom(String nom);
}
