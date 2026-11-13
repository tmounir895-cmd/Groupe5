-- LogiStock SA - Migration 03 (ventes, factures, mouvements de stock)
-- A executer UNE fois, apres 01 et 02. Sauvegarde la base avant. Non teste sur ton MySQL.
-- Les colonnes qui peuvent deja contenir des donnees restent NULL-ables pour ne pas echouer.

USE logistocksa;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) vente : cle auto-incrementee, date + heure
ALTER TABLE vente MODIFY idvente INT NOT NULL AUTO_INCREMENT;
ALTER TABLE vente MODIFY `date-vente` DATETIME DEFAULT NULL;

-- 2) ligne-vente : cle auto-incrementee, quantite entiere
ALTER TABLE `ligne-vente` MODIFY `idligne-vente` INT NOT NULL AUTO_INCREMENT;
ALTER TABLE `ligne-vente` MODIFY `quantité` INT DEFAULT NULL;

--    Les index UNIQUE sur idvente et idproduit limitaient une vente a UNE ligne
--    et un produit a UNE seule vente. On les remplace par des index normaux
--    (les cles etrangeres ont besoin d'un index).
ALTER TABLE `ligne-vente` ADD INDEX idx_lv_vente (idvente), ADD INDEX idx_lv_produit (idproduit);
ALTER TABLE `ligne-vente` DROP INDEX idvente_UNIQUE, DROP INDEX idproduit_UNIQUE;

-- 3) facture : cle auto-incrementee, numero au format texte (ex. F2026-000012), une facture par vente
ALTER TABLE facture MODIFY idfacture INT NOT NULL AUTO_INCREMENT;
ALTER TABLE facture MODIFY `numero-facture` VARCHAR(20) DEFAULT NULL;
ALTER TABLE facture ADD CONSTRAINT uq_facture_vente UNIQUE (idvente);

-- 4) mouvement-stock : cle auto-incrementee, quantite entiere, date + heure
ALTER TABLE `mouvement-stock` MODIFY `idmouvement-stock` INT NOT NULL AUTO_INCREMENT;
ALTER TABLE `mouvement-stock` MODIFY `quantité` INT DEFAULT NULL;
ALTER TABLE `mouvement-stock` MODIFY `date-mouvement` DATETIME DEFAULT NULL;

SET FOREIGN_KEY_CHECKS = 1;

-- 5) Verification : les 4 tables doivent avoir une colonne id en auto_increment
SHOW COLUMNS FROM vente;
