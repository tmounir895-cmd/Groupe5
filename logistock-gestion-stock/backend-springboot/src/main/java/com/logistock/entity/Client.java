package com.logistock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Table `client`. Le code (ex. C0001) est généré par l'application. */
@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @Column(name = "idclient", length = 5)
    private String id;

    @Column(name = "nom", length = 45, nullable = false)
    private String nom;

    @Column(name = "telephone", length = 20, nullable = false, unique = true)
    private String telephone;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "adresse", length = 255, nullable = false)
    private String adresse;
}
