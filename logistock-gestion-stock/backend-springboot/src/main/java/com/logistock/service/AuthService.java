package com.logistock.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.LoginRequest;
import com.logistock.dto.LoginResponse;
import com.logistock.dto.UtilisateurResponse;
import com.logistock.entity.Utilisateur;
import com.logistock.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String MESSAGE = "Email ou mot de passe incorrect.";

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {
        // Même message si l'email n'existe pas ou si le mot de passe est faux : on ne révèle rien.
        Utilisateur u = utilisateurRepository.findByEmailIgnoreCase(req.email().trim())
                .orElseThrow(() -> new BadCredentialsException(MESSAGE));
        if (!passwordEncoder.matches(req.motDePasse(), u.getMotDePasse())) {
            throw new BadCredentialsException(MESSAGE);
        }
        return new LoginResponse(jwtService.generer(u), "Bearer", jwtService.dureeSecondes(),
                UtilisateurResponse.from(u));
    }
}
