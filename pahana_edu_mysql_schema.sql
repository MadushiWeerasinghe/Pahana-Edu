-- Run this in XAMPP's phpMyAdmin (MySQL 8+)
CREATE DATABASE IF NOT EXISTS pahana_edu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pahana_edu;

DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;

CREATE TABLE users(
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role ENUM('ADMIN','CASHIER') NOT NULL
);

CREATE TABLE customers(
  id INT AUTO_INCREMENT PRIMARY KEY,
  account_number VARCHAR(30) UNIQUE NOT NULL,
  name VARCHAR(120) NOT NULL,
  address VARCHAR(255),
  phone VARCHAR(30)
);

CREATE TABLE items(
  id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(30) UNIQUE NOT NULL,
  description VARCHAR(255) NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL
);

CREATE TABLE bills(
  id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  sub_total DECIMAL(10,2) NOT NULL,
  tax DECIMAL(10,2) NOT NULL,
  grand_total DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE bill_items(
  id INT AUTO_INCREMENT PRIMARY KEY,
  bill_id INT NOT NULL,
  item_id INT NOT NULL,
  qty INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  description VARCHAR(255) NOT NULL,
  FOREIGN KEY (bill_id) REFERENCES bills(id),
  FOREIGN KEY (item_id) REFERENCES items(id)
);

-- seed users (password: admin123 / cashier123) - hashes will be replaced from README instructions
INSERT INTO users(username, password_hash, role) VALUES
('admin', 'TO_BE_REPLACED', 'ADMIN'),
('cashier', 'TO_BE_REPLACED', 'CASHIER');


