-- LogiStock SA - Migration 02 (utilisateurs, roles, JWT) - VERSION CORRIGEE
-- Adaptee a ta table role existante : r01 administrateur, r02 gestionnaire, r03 magasinier, r04 caissier.
-- A executer UNE seule fois. Sauvegarde la base avant. Non teste sur ton MySQL.

USE logistocksa;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) role : on garde tes codes r01..r04 et on renomme avec les noms attendus par l'application
UPDATE role SET nomrole = 'ADMINISTRATEUR'     WHERE idrole = 'r01';
UPDATE role SET nomrole = 'RESPONSABLE_STOCK'  WHERE idrole = 'r02';
UPDATE role SET nomrole = 'GESTIONNAIRE_STOCK' WHERE idrole = 'r03';
UPDATE role SET nomrole = 'VENDEUR'            WHERE idrole = 'r04';
ALTER TABLE role MODIFY nomrole VARCHAR(45) NOT NULL;
ALTER TABLE role ADD CONSTRAINT uq_role_nom UNIQUE (nomrole);

-- 2) utilisateur : cle auto-incrementee, email assez long et unique,
--    mot de passe = hash BCrypt (60 caracteres) donc VARCHAR et non DECIMAL(5,0)
ALTER TABLE utilisateur MODIFY idutilisateur INT NOT NULL AUTO_INCREMENT;
ALTER TABLE utilisateur MODIFY email VARCHAR(100) NOT NULL;
ALTER TABLE utilisateur ADD CONSTRAINT uq_utilisateur_email UNIQUE (email);
ALTER TABLE utilisateur MODIFY `mot-de-passe` VARCHAR(100) NOT NULL;

-- 3) Cles etrangeres : RESTRICT au lieu de CASCADE.
--    Avec CASCADE, supprimer un utilisateur effacerait aussi ses ventes et ses mouvements de stock.
ALTER TABLE utilisateur DROP FOREIGN KEY idrole;
ALTER TABLE utilisateur MODIFY idrole VARCHAR(5) NOT NULL;
ALTER TABLE utilisateur ADD CONSTRAINT idrole
    FOREIGN KEY (idrole) REFERENCES role (idrole)
    ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE vente DROP FOREIGN KEY `fk-vent-utilisateur`;
ALTER TABLE vente ADD CONSTRAINT `fk-vent-utilisateur`
    FOREIGN KEY (idutilisateur) REFERENCES utilisateur (idutilisateur)
    ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `mouvement-stock` DROP FOREIGN KEY idutilisateur;
ALTER TABLE `mouvement-stock` ADD CONSTRAINT idutilisateur
    FOREIGN KEY (idutilisateur) REFERENCES utilisateur (idutilisateur)
    ON DELETE RESTRICT ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- 4) Verification : tu dois voir r01 ADMINISTRATEUR, r02 RESPONSABLE_STOCK, r03 GESTIONNAIRE_STOCK, r04 VENDEUR
SELECT * FROM role;

-- 5) ETAPE SEPAREE, a lancer APRES ce script : supprimer les 4 anciens comptes
--    (l'application creera alors admin@logistock.sa au prochain demarrage).
--    Grace au RESTRICT ci-dessus, MySQL REFUSE si l'un d'eux a deja une vente ou un mouvement de stock.
-- DELETE FROM utilisateur;
