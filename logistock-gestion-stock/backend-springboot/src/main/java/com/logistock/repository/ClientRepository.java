package com.logistock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logistock.entity.Client;

public interface ClientRepository extends JpaRepository<Client, String> {

    boolean existsByTelephone(String telephone);

    boolean existsByTelephoneAndIdNot(String telephone, String id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);

    /** Recherche par nom ou téléphone. q nul = tous les clients. */
    @Query("""
        select c from Client c
        where :q is null
           or lower(c.nom) like lower(concat('%', :q, '%'))
           or c.telephone like concat('%', :q, '%')
        order by c.nom asc
        """)
    List<Client> rechercher(@Param("q") String q);
}
