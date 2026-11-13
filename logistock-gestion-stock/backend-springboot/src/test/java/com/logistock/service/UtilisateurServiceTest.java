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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.logistock.dto.PasswordResetRequest;
import com.logistock.dto.RoleUpdateRequest;
import com.logistock.dto.UtilisateurCreateRequest;
import com.logistock.dto.UtilisateurResponse;
import com.logistock.entity.Role;
import com.logistock.entity.Utilisateur;
import com.logistock.exception.ConflictException;
import com.logistock.repository.RoleRepository;
import com.logistock.repository.UtilisateurRepository;
import com.logistock.security.RoleNom;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock UtilisateurRepository utilisateurRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UtilisateurService service;

    private UtilisateurCreateRequest requete() {
        return new UtilisateurCreateRequest("Diallo", "Awa", "awa@logistock.ml", "motdepasse1", RoleNom.VENDEUR);
    }

    @Test
    void creer_refuse_un_email_deja_utilise() {
        when(utilisateurRepository.existsByEmailIgnoreCase("awa@logistock.ml")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(requete()));
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void creer_enregistre_un_hash_et_le_role_demande() {
        when(utilisateurRepository.existsByEmailIgnoreCase("awa@logistock.ml")).thenReturn(false);
        when(roleRepository.findByNom("VENDEUR")).thenReturn(Optional.of(Role.builder().id("VEN").nom("VENDEUR").build()));
        when(passwordEncoder.encode("motdepasse1")).thenReturn("HASH");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        UtilisateurResponse r = service.creer(requete());

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(captor.capture());
        assertEquals("HASH", captor.getValue().getMotDePasse());
        assertEquals("VENDEUR", r.role());
    }

    @Test
    void modifier_son_propre_role_est_refuse() {
        assertThrows(ConflictException.class,
                () -> service.modifierRole(7, new RoleUpdateRequest(RoleNom.VENDEUR), 7));
    }

    @Test
    void supprimer_son_propre_compte_est_refuse() {
        assertThrows(ConflictException.class, () -> service.supprimer(7, 7));
        verify(utilisateurRepository, never()).delete(any());
    }

    @Test
    void reinitialiser_mot_de_passe_enregistre_un_nouveau_hash() {
        Utilisateur u = Utilisateur.builder().id(2).motDePasse("67").build();
        when(utilisateurRepository.findById(2)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("nouveaumdp1")).thenReturn("HASH2");

        service.reinitialiserMotDePasse(2, new PasswordResetRequest("nouveaumdp1"));

        assertEquals("HASH2", u.getMotDePasse());
    }
}
