package com.logistock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.logistock.dto.LoginRequest;
import com.logistock.dto.LoginResponse;
import com.logistock.entity.Role;
import com.logistock.entity.Utilisateur;
import com.logistock.repository.UtilisateurRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UtilisateurRepository utilisateurRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthService service;

    private Utilisateur utilisateur() {
        Role role = Role.builder().id("VEN").nom("VENDEUR").build();
        return Utilisateur.builder().id(1).nom("Diallo").prenom("Awa")
                .email("awa@logistock.ml").motDePasse("hash").role(role).build();
    }

    @Test
    void email_inconnu_est_refuse() {
        when(utilisateurRepository.findByEmailIgnoreCase("x@y.ml")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("x@y.ml", "motdepasse1")));
    }

    @Test
    void mauvais_mot_de_passe_est_refuse_sans_creer_de_jeton() {
        when(utilisateurRepository.findByEmailIgnoreCase("awa@logistock.ml")).thenReturn(Optional.of(utilisateur()));
        when(passwordEncoder.matches("mauvais", "hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("awa@logistock.ml", "mauvais")));
        verify(jwtService, never()).generer(any());
    }

    @Test
    void identifiants_valides_renvoient_un_jeton_et_le_role() {
        when(utilisateurRepository.findByEmailIgnoreCase("awa@logistock.ml")).thenReturn(Optional.of(utilisateur()));
        when(passwordEncoder.matches("bonmotdepasse", "hash")).thenReturn(true);
        when(jwtService.generer(any(Utilisateur.class))).thenReturn("jwt");
        when(jwtService.dureeSecondes()).thenReturn(3600L);

        LoginResponse r = service.login(new LoginRequest("awa@logistock.ml", "bonmotdepasse"));

        assertEquals("jwt", r.token());
        assertEquals("Bearer", r.type());
        assertEquals("VENDEUR", r.utilisateur().role());
    }
}
