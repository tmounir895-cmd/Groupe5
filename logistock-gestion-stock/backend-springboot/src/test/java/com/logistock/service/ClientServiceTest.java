package com.logistock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistock.dto.ClientRequest;
import com.logistock.dto.ClientResponse;
import com.logistock.entity.Client;
import com.logistock.exception.ConflictException;
import com.logistock.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock ClientRepository clientRepository;
    @InjectMocks ClientService service;

    private ClientRequest requete(String telephone, String email) {
        return new ClientRequest("Awa Diallo", telephone, email, "Bamako");
    }

    @Test
    void creer_genere_un_code_retire_les_espaces_du_telephone_et_met_un_email_vide_a_null() {
        when(clientRepository.existsByTelephone("70123456")).thenReturn(false);
        when(clientRepository.count()).thenReturn(0L);
        when(clientRepository.existsById("C0001")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        ClientResponse r = service.creer(requete("70 12 34 56", "  "));

        assertEquals("C0001", r.id());
        assertEquals("70123456", r.telephone());
        assertNull(r.email());
    }

    @Test
    void creer_saute_les_codes_deja_pris() {
        when(clientRepository.existsByTelephone("70123456")).thenReturn(false);
        when(clientRepository.count()).thenReturn(1L);
        when(clientRepository.existsById("C0002")).thenReturn(true);
        when(clientRepository.existsById("C0003")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("C0003", service.creer(requete("70123456", null)).id());
    }

    @Test
    void creer_refuse_un_telephone_deja_utilise() {
        when(clientRepository.existsByTelephone("70123456")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(requete("70123456", null)));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void creer_refuse_un_email_deja_utilise() {
        when(clientRepository.existsByTelephone("70123456")).thenReturn(false);
        when(clientRepository.existsByEmailIgnoreCase("awa@mail.ml")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(requete("70123456", "awa@mail.ml")));
        verify(clientRepository, never()).save(any());
    }
}
