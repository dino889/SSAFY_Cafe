-- MySQL Script generated by MySQL Workbench
-- Tue Nov 23 21:28:00 2021
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema ssafy_cafe
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `ssafy_cafe` ;

-- -----------------------------------------------------
-- Schema ssafy_cafe
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ssafy_cafe` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `ssafy_cafe` ;

-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_user` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_user` (
  `id` VARCHAR(100) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `pass` VARCHAR(100) NOT NULL,
  `stamps` INT NULL DEFAULT 0,
  `phone` VARCHAR(45) NULL,
  `money` INT NOT NULL DEFAULT 0,
  `token` VARCHAR(200) NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_product_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_product_type` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_product_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type_name` VARCHAR(20) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_product`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_product` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_product` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `type` INT NOT NULL,
  `price` INT NOT NULL,
  `img` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_product_product_type_idx` (`type` ASC) VISIBLE,
  CONSTRAINT `fk_product_product_type`
    FOREIGN KEY (`type`)
    REFERENCES `ssafy_cafe`.`t_product_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE RESTRICT);


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_order`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_order` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_order` (
  `o_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL,
  `order_table` VARCHAR(20) NULL DEFAULT NULL,
  `order_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`o_id`),
  INDEX `fk_order_user` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ssafy_cafe`.`t_user` (`id`)
    ON DELETE cascade);


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_order_detail`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_order_detail` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_order_detail` (
  `d_id` INT NOT NULL AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `type` TINYINT NULL,
  `syrup` VARCHAR(20) NULL,
  `shot` INT NULL,
  `commentdupchk` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`d_id`),
  INDEX `fk_order_detail_order` (`order_id` ASC) VISIBLE,
  INDEX `fk_order_detail_product_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_detail_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `ssafy_cafe`.`t_product` (`id`)
    ON DELETE cascade,
  CONSTRAINT `fk_order_detail_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `ssafy_cafe`.`t_order` (`o_id`)
    ON DELETE cascade);


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_stamp`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_stamp` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_stamp` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL,
  `order_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `fk_stamp_user` (`user_id` ASC) VISIBLE,
  INDEX `fk_stamp_order` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_stamp_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ssafy_cafe`.`t_user` (`id`)
    ON DELETE cascade,
  CONSTRAINT `fk_stamp_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `ssafy_cafe`.`t_order` (`o_id`)
    ON DELETE cascade);


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_comment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_comment` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL,
  `product_id` INT NOT NULL,
  `rating` FLOAT NOT NULL DEFAULT 1,
  `comment` VARCHAR(200) NOT NULL,
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_comment_user` (`user_id` ASC) VISIBLE,
  INDEX `fk_comment_product` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_comment_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ssafy_cafe`.`t_user` (`id`)
    ON DELETE cascade
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `ssafy_cafe`.`t_product` (`id`)
    ON DELETE cascade);


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_notification`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_notification` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_notification` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL,
  `category` VARCHAR(20) NOT NULL,
  `content` VARCHAR(200) NOT NULL,
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_notification_user_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_notification_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ssafy_cafe`.`t_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ssafy_cafe`.`t_user_custom`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ssafy_cafe`.`t_user_custom` ;

CREATE TABLE IF NOT EXISTS `ssafy_cafe`.`t_user_custom` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL,
  `product_id` INT NOT NULL,
  `type` TINYINT NOT NULL,
  `syrup` VARCHAR(20) NULL,
  `shot` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_custom_user_idx` (`user_id` ASC) VISIBLE,
  INDEX `fk_user_custom_product_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_custom_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ssafy_cafe`.`t_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_custom_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `ssafy_cafe`.`t_product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
