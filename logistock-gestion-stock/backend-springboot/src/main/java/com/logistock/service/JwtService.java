package com.logistock.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.logistock.entity.Utilisateur;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expirationMinutes;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.encoder = encoder;
        this.expirationMinutes = expirationMinutes;
    }

    /** Jeton contenant : email (sub), identifiant (uid) et rôle (role). */
    public String generer(Utilisateur u) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("logistock")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(u.getEmail())
                .claim("uid", u.getId());
        if (u.getRole() != null) {
            claims.claim("role", u.getRole().getNom());
        }
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }

    public long dureeSecondes() {
        return expirationMinutes * 60;
    }
}
