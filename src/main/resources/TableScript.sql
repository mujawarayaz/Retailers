DROP DATABASE IF EXISTS retail_db;
CREATE DATABASE retail_db;
USE retail_db;


CREATE TABLE Customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE Retailer (
    retailer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    retailer_id INT NOT NULL,
    customer_id INT NOT NULL,
    amount_spent DECIMAL(10, 2) NOT NULL,
    reward_point INT DEFAULT 0,
    transaction_date DATE NOT NULL,
    CONSTRAINT fk_retailer FOREIGN KEY (retailer_id) REFERENCES Retailer(retailer_id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);


ALTER TABLE Customer AUTO_INCREMENT = 101;
ALTER TABLE Retailer AUTO_INCREMENT = 101;

INSERT INTO Customer (name) VALUES ('Ayaz Mujawar');
INSERT INTO Retailer (name) VALUES ('Aman Momin');   