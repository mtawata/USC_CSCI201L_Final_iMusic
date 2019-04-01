DROP DATABASE IF EXISTS `imusic`;
CREATE DATABASE `imusic`;

USE imusic;

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `username` varchar(25) NOT NULL DEFAULT '',
  `password` varchar(64) NOT NULL DEFAULT '',
  `name` varchar(25) NOT NULL DEFAULT '',
  `bio` text,
  `songs` text,
  `picture` text,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;