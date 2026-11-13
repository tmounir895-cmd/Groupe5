package com.logistock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Table `utilisateur`. Pas de @ToString : le hash du mot de passe ne doit jamais finir dans un log. */
@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutilisateur")
    private Integer id;

    @Column(name = "`nom-utilisateur`", length = 45, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 45)
    private String prenom;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    /** Hash BCrypt (jamais le mot de passe en clair). */
    @Column(name = "`mot-de-passe`", length = 100, nullable = false)
    private String motDePasse;

    @ManyToOne
    @JoinColumn(name = "idrole")
    private Role role;
}
