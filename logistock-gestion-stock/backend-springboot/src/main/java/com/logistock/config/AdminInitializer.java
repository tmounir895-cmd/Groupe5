package com.logistock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.entity.Role;
import com.logistock.entity.Utilisateur;
import com.logistock.repository.RoleRepository;
import com.logistock.repository.UtilisateurRepository;
import com.logistock.security.RoleNom;

import lombok.extern.slf4j.Slf4j;

/**
 * Au démarrage, crée le compte administrateur configuré (app.bootstrap-admin.email) s'il n'existe pas encore,
 * pour que quelqu'un puisse se connecter et créer les autres comptes. Ne fait rien s'il existe déjà.
 */
@Slf4j
@Component
public class AdminInitializer implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String nom;
    private final String password;

    public AdminInitializer(UtilisateurRepository utilisateurRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.bootstrap-admin.email}") String email,
                            @Value("${app.bootstrap-admin.nom}") String nom,
                            @Value("${app.bootstrap-admin.password}") String password) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.nom = nom;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (utilisateurRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        if (password == null || password.isBlank()) {
            log.warn("Le compte administrateur n'existe pas et app.bootstrap-admin.password est vide : "
                    + "renseigne-le dans application.properties pour le créer.");
            return;
        }
        Role role = roleRepository.findByNom(RoleNom.ADMINISTRATEUR.name()).orElse(null);
        if (role == null) {
            log.error("Rôle ADMINISTRATEUR introuvable : exécute database/02_migration_utilisateurs_roles.sql.");
            return;
        }
        utilisateurRepository.save(Utilisateur.builder()
                .nom(nom)
                .email(email)
                .motDePasse(passwordEncoder.encode(password))
                .role(role)
                .build());
        log.info("Premier administrateur créé : {}", email);
    }
}
