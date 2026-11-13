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

/** Table `role` : idrole = code court (ADM, VEN, GES, RES), nomrole = ADMINISTRATEUR, VENDEUR... */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @Column(name = "idrole", length = 5)
    private String id;

    @Column(name = "nomrole", length = 45, nullable = false, unique = true)
    private String nom;
}
