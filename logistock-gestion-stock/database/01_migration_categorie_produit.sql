-- LogiStock SA - Migration 01 (module Categorie / Produit)
-- A executer UNE fois sur le schema logistocksa (tables vides au moment de l'ecriture).
-- Non teste sur ton MySQL : lis-le, sauvegarde la base, puis execute-le.

USE logistocksa;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) produit : cle primaire auto-incrementee (le dump n'a pas AUTO_INCREMENT)
ALTER TABLE produit MODIFY idproduit INT NOT NULL AUTO_INCREMENT;

-- 2) produit : quantite et seuil d'alerte en vrais entiers (seul-alerte etait un varchar)
ALTER TABLE produit MODIFY `quantité-stock` INT NOT NULL DEFAULT 0;
ALTER TABLE produit MODIFY `seul-alerte`    INT NOT NULL DEFAULT 0;

-- 3) produit : reference unique + garde-fous (MySQL 8.0.16+ applique les CHECK)
ALTER TABLE produit ADD CONSTRAINT uq_produit_reference UNIQUE (reference);
ALTER TABLE produit ADD CONSTRAINT chk_produit_quantite CHECK (`quantité-stock` >= 0);
ALTER TABLE produit ADD CONSTRAINT chk_produit_seuil    CHECK (`seul-alerte` >= 0);
ALTER TABLE produit ADD CONSTRAINT chk_produit_prix     CHECK (prix >= 0);

-- 4) categorie : deux categories doivent pouvoir avoir la meme description ; description plus longue
ALTER TABLE categorie DROP INDEX description_UNIQUE;
ALTER TABLE categorie MODIFY description VARCHAR(255) DEFAULT NULL;

-- 5) Cles etrangeres vers produit/categorie : ON DELETE RESTRICT au lieu de CASCADE.
--    Avec CASCADE, supprimer une categorie ou un produit effacerait en silence
--    les produits, lignes de vente et mouvements de stock associes.
ALTER TABLE produit DROP FOREIGN KEY idcategorie;
ALTER TABLE produit ADD CONSTRAINT idcategorie
    FOREIGN KEY (idcategorie) REFERENCES categorie (idcategorie)
    ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `ligne-vente` DROP FOREIGN KEY `fk-ligne-vente-produit`;
ALTER TABLE `ligne-vente` ADD CONSTRAINT `fk-ligne-vente-produit`
    FOREIGN KEY (idproduit) REFERENCES produit (idproduit)
    ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `mouvement-stock` DROP FOREIGN KEY idproduit;
ALTER TABLE `mouvement-stock` ADD CONSTRAINT idproduit
    FOREIGN KEY (idproduit) REFERENCES produit (idproduit)
    ON DELETE RESTRICT ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;
