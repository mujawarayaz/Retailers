DROP DATABASE IF EXISTS retail_db;

CREATE DATABASE retail_db;

USE retail_db;


CREATE TABLE Retailer (
    retailer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    reward_point INT DEFAULT 0
);


CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    retailer_id INT NOT NULL,
    amount_spent DECIMAL(10,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_retailer FOREIGN KEY (retailer_id) REFERENCES Retailer(retailer_id) ON DELETE CASCADE
);

INSERT INTO Retailer (name, reward_point) VALUES ('Test Retailer', 0);
INSERT INTO Retailer (name, reward_point) VALUES ('Second Retailer', 0);


