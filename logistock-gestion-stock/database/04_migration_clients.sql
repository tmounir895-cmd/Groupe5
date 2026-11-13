-- LogiStock SA - Migration 04 (clients)
-- A executer UNE fois, apres 01, 02 et 03. Sauvegarde la base avant. Non teste sur ton MySQL.

USE logistocksa;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) client : telephone en texte (un numero peut commencer par 0 ou +), email assez long,
--    adresse longue et non unique (deux clients peuvent habiter a la meme adresse)
ALTER TABLE client MODIFY telephone VARCHAR(20) NOT NULL;
ALTER TABLE client MODIFY email VARCHAR(100) DEFAULT NULL;
ALTER TABLE client MODIFY adresse VARCHAR(255) NOT NULL;
ALTER TABLE client DROP INDEX adresse_UNIQUE;

-- 2) Cle etrangere vente -> client : RESTRICT au lieu de CASCADE.
--    Avec CASCADE, supprimer un client effacerait toutes ses ventes.
ALTER TABLE vente DROP FOREIGN KEY `fk-vente-client`;
ALTER TABLE vente ADD CONSTRAINT `fk-vente-client`
    FOREIGN KEY (idclient) REFERENCES client (idclient)
    ON DELETE RESTRICT ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- 3) Verification
SHOW COLUMNS FROM client;
