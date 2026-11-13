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
-- Dumping data for table `categorie`
--

LOCK TABLES `categorie` WRITE;
/*!40000 ALTER TABLE `categorie` DISABLE KEYS */;
INSERT INTO `categorie` VALUES ('cta01','informatique','materiel informatique'),('cta02','burautique','fournitures du burau'),('cta03','mobilier','meubles et equipement'),('cta04','accessoir','accessoir divers');
/*!40000 ALTER TABLE `categorie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES ('cl01','fousseny',70412367,'fous@ml','bamako'),('cl02','amadou',70542367,'amad@ml','bamako'),('cl03','kadia',90547612,'kadia@ml','bamako'),('cl04','hama',65873412,'hama@ml','bamako');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `facture`
--

LOCK TABLES `facture` WRITE;
/*!40000 ALTER TABLE `facture` DISABLE KEYS */;
INSERT INTO `facture` VALUES (1,202303,'2021-03-12',1),(2,203451,'2023-02-13',1),(3,205463,'2021-04-24',2),(4,206533,'2025-03-01',3);
/*!40000 ALTER TABLE `facture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ligne-vente`
--

LOCK TABLES `ligne-vente` WRITE;
/*!40000 ALTER TABLE `ligne-vente` DISABLE KEYS */;
INSERT INTO `ligne-vente` VALUES (1,2,2000,1,1),(2,3,1000,2,3),(3,12,3000,4,4),(4,8,4000,3,2);
/*!40000 ALTER TABLE `ligne-vente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `mouvement-stock`
--

LOCK TABLES `mouvement-stock` WRITE;
/*!40000 ALTER TABLE `mouvement-stock` DISABLE KEYS */;
INSERT INTO `mouvement-stock` VALUES (1,'entrée',10,'2021-01-12',1,1),(2,'sortie',3,'2021-04-23',2,2),(3,'sortie',21,'2023-08-12',1,3),(4,'entré',11,'2026-10-20',4,4);
/*!40000 ALTER TABLE `mouvement-stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `produit`
--

LOCK TABLES `produit` WRITE;
/*!40000 ALTER TABLE `produit` DISABLE KEYS */;
INSERT INTO `produit` VALUES (1,'ref01','clef usb',1000,23,'3','cta01'),(2,'ref02','carte memoir',2000,23,'4','cta01'),(3,'ref','chaise',3000,34,'5','cta02'),(4,'ref','table',5000,12,'2','cta02');
/*!40000 ALTER TABLE `produit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('r01','administrateur'),('r02','gestionnaire'),('r03','magasinier'),('r04','caissier');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (1,'kone','fousseny','kone@ml',123,'r01'),(2,'diarra','amadou','amad@ml',67,'r02'),(3,'koita','amza','amza@ml',435,'r03'),(4,'zeina','maiga','zeina@ml',564,'r04');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2026-11-05  0:00:24
