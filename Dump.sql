-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: mydb
-- ------------------------------------------------------
-- Server version	5.7.10-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `answers`
--

DROP TABLE IF EXISTS `answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `answers` (
  `id_answer` int(11) NOT NULL AUTO_INCREMENT,
  `flag` tinyint(1) NOT NULL,
  `id_question` int(11) DEFAULT NULL,
  `text_answer` mediumtext,
  PRIMARY KEY (`id_answer`),
  KEY `a_q_id_idx` (`id_question`),
  CONSTRAINT `a_q_id` FOREIGN KEY (`id_question`) REFERENCES `questions` (`id_question`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=376 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answers`
--

LOCK TABLES `answers` WRITE;
/*!40000 ALTER TABLE `answers` DISABLE KEYS */;
INSERT INTO `answers` VALUES (18,1,11,'ТСО'),(19,0,11,'Не ТСО'),(20,0,11,'ОСТ'),(21,1,12,'Для О'),(22,0,12,'Для Т'),(23,0,12,'Для С'),(24,1,13,'По назначению'),(25,1,13,'Регулярно'),(26,0,13,'Временами'),(27,0,13,'Как СО'),(28,0,14,'Туннельные'),(29,0,14,'Транспортные'),(30,0,14,'Транквилизаторные'),(31,1,14,'Технические'),(235,0,26,'Дядька'),(236,1,26,'Финский алкаш'),(237,0,26,'Он как БГ'),(242,0,27,'Прямо сейчас'),(243,1,27,'Ещё вчера'),(244,0,27,'Вообще-то, Windows.'),(245,0,27,'Что я тут делаю?'),(354,0,36,'Наука'),(355,1,36,'Не наука'),(356,0,36,'Так себе развлечение'),(357,1,40,'механизмы'),(358,1,40,'физику'),(359,0,40,'твёрдые тела'),(360,1,41,'Когда всё динамично'),(361,0,41,'тоже физика'),(362,1,41,'не знаю'),(363,1,41,'аааааа'),(364,1,41,'хватит'),(367,1,42,'Ньютон'),(368,1,42,'Яблоко'),(369,1,45,'Windows'),(370,0,45,'OSX'),(371,1,46,'ТУСИ'),(372,1,46,'Закуси'),(373,1,47,'Извините'),(374,1,47,'Эээээм..'),(375,1,47,'Эээээ, скажите пожалуйста!');
/*!40000 ALTER TABLE `answers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disciplines`
--

DROP TABLE IF EXISTS `disciplines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `disciplines` (
  `id_discipline` int(11) NOT NULL AUTO_INCREMENT,
  `name_discipline` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_discipline`),
  UNIQUE KEY `name_discipline_UNIQUE` (`name_discipline`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disciplines`
--

LOCK TABLES `disciplines` WRITE;
/*!40000 ALTER TABLE `disciplines` DISABLE KEYS */;
INSERT INTO `disciplines` VALUES (3,'ЗИ'),(1,'ОС'),(2,'Физика'),(9,'Химия');
/*!40000 ALTER TABLE `disciplines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `questions` (
  `id_question` int(11) NOT NULL AUTO_INCREMENT,
  `id_vikt` int(11) DEFAULT NULL,
  `text_question` mediumtext,
  `number_of_question` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_question`),
  KEY `id_vikt_idx` (`id_vikt`),
  KEY `asfgasfsaf_idx` (`id_vikt`),
  KEY `questions_vikt_id_idx` (`id_vikt`),
  KEY `q_vikt_id_idx` (`id_vikt`),
  CONSTRAINT `q_vikt_id` FOREIGN KEY (`id_vikt`) REFERENCES `viktorines` (`id_vikt`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (11,67,'Что такое ТСО?',1),(12,67,'Для чего нужны ТСО?',2),(13,67,'Как применяются ТСО?',3),(14,67,'\"Т\" в аббревиатуре ТСО - это ...',4),(26,79,'Кто такой Линус Торвальдс?',1),(27,79,'Когда пора переустанавливать виндукс?',2),(36,90,'Физика - это ...',1),(40,90,'Механика изучает ...',2),(41,90,'Динамика - это...',3),(42,90,'Последний вопрос: кто вывел закон Ньютона?',4),(45,92,'Как правильно - Linux, Unix или GNU/Linux?',1),(46,92,'Как называется наш университет?',2),(47,92,'Как зовут преподавателя?',3);
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `authority` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_STUDENT'),(2,'ROLE_TEACHER'),(3,'ROLE_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `enabled` int(11) DEFAULT NULL,
  `hashpass` varchar(255) DEFAULT NULL,
  `login` varchar(255) NOT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `login_UNIQUE` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,1,'$2a$10$s7jaXvKSVvNd8uCDCHOM7.fSagwdGulf93OpBYrdB/8HLQUVJDWMK','administrator',NULL,3,'Administrator'),(11,1,'$2a$10$95WGpot6rRo0vB2xvWsLde4pN/mrVb..PFeEoxRq6z/pYVbvqHWO2','tkorolkova',NULL,2,'Татьяна Королькова'),(12,1,'$2a$10$zXtFNMNTCXiiQlVnYWQHduRzQ2zrmq1bWjV1Z5rJSiFzoQcm.KxsW','vpupkin',11,1,'Василий Петрович'),(13,1,'$2a$10$AWZvFBL1fNyiqUXmWaxJd.YOaezkwkMzcIBRArPVLb2FUkkJLpHwi','iivanov',11,1,'Иван Петрович'),(18,1,'$2a$10$B70g21v9/XCyIBY0DRStxeoiyMLhK9bRMBX0S32l8fKgHzNcTPZ9u','tutova',NULL,2,'Наталья Тутова'),(19,1,'$2a$10$ovHUS/eDplWPs5wgMNdiKOHIzP1i6I3WbXZnpYuptuQG8MQrChXgW','mary',18,1,'Мария'),(20,1,'$2a$10$3cxTBstQFbu7P3ebMl/UKeMWa121pPPYO8nR/jJL7QFC9iFshfRzS','alexey',18,1,'Алексей');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `viktorines`
--

DROP TABLE IF EXISTS `viktorines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `viktorines` (
  `id_vikt` int(11) NOT NULL AUTO_INCREMENT,
  `id_dicipline` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `questioncol` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_vikt`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `viktorines`
--

LOCK TABLES `viktorines` WRITE;
/*!40000 ALTER TABLE `viktorines` DISABLE KEYS */;
INSERT INTO `viktorines` VALUES (67,3,11,'ТСО',NULL),(79,1,11,'ОС 1',NULL),(90,2,11,'Физика для самых маленьких',NULL),(92,1,18,'Изучаем FreeBSD',NULL);
/*!40000 ALTER TABLE `viktorines` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-08 12:45:35
