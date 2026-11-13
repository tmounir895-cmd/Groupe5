package com.logistock.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistock.dto.ClientRequest;
import com.logistock.dto.ClientResponse;
import com.logistock.entity.Client;
import com.logistock.exception.ConflictException;
import com.logistock.exception.ResourceNotFoundException;
import com.logistock.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    public List<ClientResponse> lister(String recherche) {
        String q = (recherche == null || recherche.isBlank()) ? null : recherche.trim();
        return clientRepository.rechercher(q).stream().map(ClientResponse::from).toList();
    }

    public ClientResponse obtenir(String id) {
        return ClientResponse.from(trouver(id));
    }

    @Transactional
    public ClientResponse creer(ClientRequest req) {
        String telephone = normaliserTelephone(req.telephone());
        String email = normaliserEmail(req.email());
        if (clientRepository.existsByTelephone(telephone)) {
            throw new ConflictException("Un client avec le téléphone « " + telephone + " » existe déjà.");
        }
        if (email != null && clientRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Un client avec l'email « " + email + " » existe déjà.");
        }
        Client c = Client.builder()
                .id(nouveauCode())
                .nom(req.nom().trim())
                .telephone(telephone)
                .email(email)
                .adresse(req.adresse().trim())
                .build();
        return ClientResponse.from(clientRepository.save(c));
    }

    @Transactional
    public ClientResponse modifier(String id, ClientRequest req) {
        Client c = trouver(id);
        String telephone = normaliserTelephone(req.telephone());
        String email = normaliserEmail(req.email());
        if (clientRepository.existsByTelephoneAndIdNot(telephone, id)) {
            throw new ConflictException("Un client avec le téléphone « " + telephone + " » existe déjà.");
        }
        if (email != null && clientRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("Un client avec l'email « " + email + " » existe déjà.");
        }
        c.setNom(req.nom().trim());
        c.setTelephone(telephone);
        c.setEmail(email);
        c.setAdresse(req.adresse().trim());
        return ClientResponse.from(c);
    }

    @Transactional
    public void supprimer(String id) {
        Client c = trouver(id);
        clientRepository.delete(c);
        // flush : si le client a déjà des ventes, MySQL refuse ici (clé étrangère RESTRICT).
        clientRepository.flush();
    }

    private Client trouver(String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable : " + id));
    }

    /** Code C0001, C0002... : on part du nombre de clients et on saute les codes déjà pris. */
    private String nouveauCode() {
        long n = clientRepository.count() + 1;
        String code;
        do {
            code = String.format("C%04d", n++);
        } while (clientRepository.existsById(code));
        return code;
    }

    /** Sans espaces, pour qu'un même numéro écrit de deux façons ne crée pas deux clients. */
    private static String normaliserTelephone(String telephone) {
        return telephone.replaceAll("\\s+", "");
    }

    /** Un email vide devient null (plusieurs clients sans email sont autorisés, mais pas deux emails identiques). */
    private static String normaliserEmail(String email) {
        return (email == null || email.isBlank()) ? null : email.trim();
    }
}
