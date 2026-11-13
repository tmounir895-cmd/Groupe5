-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: logistocksa
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categorie`
--

DROP TABLE IF EXISTS `categorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorie` (
  `idcategorie` varchar(5) NOT NULL,
  `nom-categorie` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idcategorie`),
  UNIQUE KEY `nom-categorie_UNIQUE` (`nom-categorie`),
  UNIQUE KEY `description_UNIQUE` (`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorie`
--

LOCK TABLES `categorie` WRITE;
/*!40000 ALTER TABLE `categorie` DISABLE KEYS */;
INSERT INTO `categorie` VALUES ('cta01','informatique','materiel informatique'),('cta02','burautique','fournitures du burau'),('cta03','mobilier','meubles et equipement'),('cta04','accessoir','accessoir divers');
/*!40000 ALTER TABLE `categorie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `idclient` varchar(5) NOT NULL,
  `nom` varchar(45) NOT NULL,
  `telephone` decimal(8,0) NOT NULL,
  `email` varchar(10) DEFAULT NULL,
  `adresse` varchar(11) NOT NULL,
  PRIMARY KEY (`idclient`),
  UNIQUE KEY `telephone_UNIQUE` (`telephone`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES ('cl01','fousseny',70412367,'fous@ml','bamako'),('cl02','amadou',70542367,'amad@ml','bamako'),('cl03','kadia',90547612,'kadia@ml','bamako'),('cl04','hama',65873412,'hama@ml','bamako');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facture`
--

DROP TABLE IF EXISTS `facture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facture` (
  `idfacture` int NOT NULL,
  `numero-facture` decimal(10,0) DEFAULT NULL,
  `date-emession` date DEFAULT NULL,
  `idvente` int DEFAULT NULL,
  PRIMARY KEY (`idfacture`),
  UNIQUE KEY `numero-facture_UNIQUE` (`numero-facture`),
  KEY `idvente_idx` (`idvente`),
  CONSTRAINT `idvente` FOREIGN KEY (`idvente`) REFERENCES `vente` (`idvente`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facture`
--

LOCK TABLES `facture` WRITE;
/*!40000 ALTER TABLE `facture` DISABLE KEYS */;
INSERT INTO `facture` VALUES (1,202303,'2021-03-12',1),(2,203451,'2023-02-13',1),(3,205463,'2021-04-24',2),(4,206533,'2025-03-01',3);
/*!40000 ALTER TABLE `facture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ligne-vente`
--

DROP TABLE IF EXISTS `ligne-vente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ligne-vente` (
  `idligne-vente` int NOT NULL,
  `quantité` decimal(11,0) DEFAULT NULL,
  `prix-unitaire` decimal(10,0) DEFAULT NULL,
  `idvente` int DEFAULT NULL,
  `idproduit` int DEFAULT NULL,
  PRIMARY KEY (`idligne-vente`),
  UNIQUE KEY `idvente_UNIQUE` (`idvente`),
  UNIQUE KEY `idproduit_UNIQUE` (`idproduit`),
  CONSTRAINT ` fk-ligne-vente-vent` FOREIGN KEY (`idvente`) REFERENCES `vente` (`idvente`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk-ligne-vente-produit` FOREIGN KEY (`idproduit`) REFERENCES `produit` (`idproduit`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ligne-vente`
--

LOCK TABLES `ligne-vente` WRITE;
/*!40000 ALTER TABLE `ligne-vente` DISABLE KEYS */;
INSERT INTO `ligne-vente` VALUES (1,2,2000,1,1),(2,3,1000,2,3),(3,12,3000,4,4),(4,8,4000,3,2);
/*!40000 ALTER TABLE `ligne-vente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mouvement-stock`
--

DROP TABLE IF EXISTS `mouvement-stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mouvement-stock` (
  `idmouvement-stock` int NOT NULL,
  `type-mouvement` varchar(10) NOT NULL,
  `quantité` decimal(10,0) DEFAULT NULL,
  `date-mouvement` date DEFAULT NULL,
  `idutilisateur` int DEFAULT NULL,
  `idproduit` int DEFAULT NULL,
  PRIMARY KEY (`idmouvement-stock`),
  KEY `idutilisateur_idx` (`idutilisateur`),
  KEY `idproduit_idx` (`idproduit`),
  CONSTRAINT `idproduit` FOREIGN KEY (`idproduit`) REFERENCES `produit` (`idproduit`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idutilisateur` FOREIGN KEY (`idutilisateur`) REFERENCES `utilisateur` (`idutilisateur`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mouvement-stock`
--

LOCK TABLES `mouvement-stock` WRITE;
/*!40000 ALTER TABLE `mouvement-stock` DISABLE KEYS */;
INSERT INTO `mouvement-stock` VALUES (1,'entrée',10,'2021-01-12',1,1),(2,'sortie',3,'2021-04-23',2,2),(3,'sortie',21,'2023-08-12',1,3),(4,'entré',11,'2026-10-20',4,4);
/*!40000 ALTER TABLE `mouvement-stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produit`
--

DROP TABLE IF EXISTS `produit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produit` (
  `idproduit` int NOT NULL,
  `reference` varchar(45) NOT NULL,
  `designation` varchar(40) NOT NULL,
  `prix` decimal(10,0) NOT NULL,
  `quantité-stock` decimal(10,0) NOT NULL,
  `seul-alerte` varchar(45) NOT NULL,
  `idcategorie` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`idproduit`),
  KEY `idcategorie_idx` (`idcategorie`),
  CONSTRAINT `idcategorie` FOREIGN KEY (`idcategorie`) REFERENCES `categorie` (`idcategorie`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produit`
--

LOCK TABLES `produit` WRITE;
/*!40000 ALTER TABLE `produit` DISABLE KEYS */;
INSERT INTO `produit` VALUES (1,'ref01','clef usb',1000,23,'3','cta01'),(2,'ref02','carte memoir',2000,23,'4','cta01'),(3,'ref','chaise',3000,34,'5','cta02'),(4,'ref','table',5000,12,'2','cta02');
/*!40000 ALTER TABLE `produit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `idrole` varchar(5) NOT NULL,
  `nomrole` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idrole`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('r01','administrateur'),('r02','gestionnaire'),('r03','magasinier'),('r04','caissier');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateur` (
  `idutilisateur` int NOT NULL,
  `nom-utilisateur` varchar(45) NOT NULL,
  `prenom` varchar(45) DEFAULT NULL,
  `email` varchar(11) NOT NULL,
  `mot-de-passe` decimal(5,0) NOT NULL,
  `idrole` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`idutilisateur`),
  KEY `idrole_idx` (`idrole`),
  CONSTRAINT `idrole` FOREIGN KEY (`idrole`) REFERENCES `role` (`idrole`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (1,'kone','fousseny','kone@ml',123,'r01'),(2,'diarra','amadou','amad@ml',67,'r02'),(3,'koita','amza','amza@ml',435,'r03'),(4,'zeina','maiga','zeina@ml',564,'r04');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vente`
--

DROP TABLE IF EXISTS `vente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vente` (
  `idvente` int NOT NULL,
  `date-vente` date DEFAULT NULL,
  `idutilisateur` int DEFAULT NULL,
  `idclient` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`idvente`),
  KEY `idutilisateur_idx` (`idutilisateur`),
  KEY `idclient_idx` (`idclient`),
  CONSTRAINT `fk-vent-utilisateur` FOREIGN KEY (`idutilisateur`) REFERENCES `utilisateur` (`idutilisateur`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk-vente-client` FOREIGN KEY (`idclient`) REFERENCES `client` (`idclient`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vente`
--

LOCK TABLES `vente` WRITE;
/*!40000 ALTER TABLE `vente` DISABLE KEYS */;
INSERT INTO `vente` VALUES (1,'2023-10-12',1,'cl01'),(2,'2013-04-12',2,'cl01'),(3,'2015-11-13',1,'cl02'),(4,'2026-12-25',4,'cl04');
/*!40000 ALTER TABLE `vente` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-07 13:31:36
