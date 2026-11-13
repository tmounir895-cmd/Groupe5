package com.logistock.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.PasswordResetRequest;
import com.logistock.dto.RoleUpdateRequest;
import com.logistock.dto.UtilisateurCreateRequest;
import com.logistock.dto.UtilisateurResponse;
import com.logistock.entity.Role;
import com.logistock.entity.Utilisateur;
import com.logistock.exception.ConflictException;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.RoleRepository;
import com.logistock.repository.UtilisateurRepository;
import com.logistock.security.RoleNom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UtilisateurResponse> lister() {
        return utilisateurRepository.findAllByOrderByNomAsc().stream().map(UtilisateurResponse::from).toList();
    }

    public UtilisateurResponse obtenir(Integer id) {
        return UtilisateurResponse.from(trouver(id));
    }

    /** U1 : l'administrateur crée un compte et lui donne un rôle. */
    @Transactional
    public UtilisateurResponse creer(UtilisateurCreateRequest req) {
        String email = req.email().trim();
        if (utilisateurRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Un compte avec l'email « " + email + " » existe déjà.");
        }
        Utilisateur u = Utilisateur.builder()
                .nom(req.nom().trim())
                .prenom(req.prenom() == null || req.prenom().isBlank() ? null : req.prenom().trim())
                .email(email)
                .motDePasse(passwordEncoder.encode(req.motDePasse()))
                .role(trouverRole(req.role()))
                .build();
        return UtilisateurResponse.from(utilisateurRepository.save(u));
    }

    /** U2 : l'administrateur change le rôle d'un utilisateur (pas le sien : on évite de se retrouver sans admin). */
    @Transactional
    public UtilisateurResponse modifierRole(Integer id, RoleUpdateRequest req, Integer idUtilisateurConnecte) {
        if (id.equals(idUtilisateurConnecte)) {
            throw new ConflictException("Vous ne pouvez pas modifier votre propre rôle.");
        }
        Utilisateur u = trouver(id);
        u.setRole(trouverRole(req.role()));
        return UtilisateurResponse.from(u);
    }

    /** L'administrateur définit un nouveau mot de passe (haché) pour un compte, par exemple un ancien compte. */
    @Transactional
    public void reinitialiserMotDePasse(Integer id, PasswordResetRequest req) {
        Utilisateur u = trouver(id);
        u.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
    }

    @Transactional
    public void supprimer(Integer id, Integer idUtilisateurConnecte) {
        if (id.equals(idUtilisateurConnecte)) {
            throw new ConflictException("Vous ne pouvez pas supprimer votre propre compte.");
        }
        Utilisateur u = trouver(id);
        utilisateurRepository.delete(u);
        // flush : si l'utilisateur a déjà des ventes ou des mouvements, MySQL refuse ici.
        utilisateurRepository.flush();
    }

    private Utilisateur trouver(Integer id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }

    private Role trouverRole(RoleNom nom) {
        return roleRepository.findByNom(nom.name()).orElseThrow(() -> new ResourceNotFoundException(
                "Rôle introuvable en base : " + nom + " (script 02_migration_utilisateurs_roles.sql exécuté ?)"));
    }
}
