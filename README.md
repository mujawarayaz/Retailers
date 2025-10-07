# **Customer Reward Points System**

This is a Spring Boot application designed to calculate reward points for customer transactions over a three-month period.

## **Reward Calculation Rules**

Reward points are calculated based on the **whole dollar amount** spent in a single transaction:

1. **2 points** for every dollar spent **over $100**.  
2. **1 point** for every dollar spent **between $50 and $100**.  
3. **0 points** for amounts under $50.

**Example: $120 Transaction**

* Total Points: 90  
* ($50 \\times 1 \\text{ pt} \= 50 \\text{ pts}) \+ (20×2 pts=40 pts)

## **Project Setup**

### **Prerequisites**

* Java 17+, Maven, and a running MySQL instance.

### **Database Configuration**

Database credentials are loaded securely via environment variables:

| Property | Default Value | Description |
| :---- | :---- | :---- |
| DB\_USERNAME | root | Database username. |
| DB\_PASSWORD | root | Database password. |

### **Database Schema & Seed Data**

Run the following SQL to set up the database structure and initial retailer data:

CREATE DATABASE retail\_db;  
USE retail\_db;

CREATE TABLE Retailer (  
    retailer\_id INT AUTO\_INCREMENT PRIMARY KEY,  
    name VARCHAR(100) NOT NULL,  
    reward\_point INT DEFAULT 0  
);

CREATE TABLE Transactions (  
    transaction\_id INT AUTO\_INCREMENT PRIMARY KEY,  
    retailer\_id INT NOT NULL,  
    amount\_spent DECIMAL(10,2) NOT NULL,  
    transaction\_date TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,  
    CONSTRAINT fk\_retailer FOREIGN KEY (retailer\_id) REFERENCES Retailer(retailer\_id) ON DELETE CASCADE  
);

INSERT INTO Retailer (name, reward\_point) VALUES ('Test Retailer', 0);  
INSERT INTO Retailer (name, reward\_point) VALUES ('Second Retailer', 0);

## **API Endpoints & Samples**

The base URL for the application is **http://localhost:8080/retail**.

### **1\. POST: Record Transaction**

Records a new transaction, calculates points, and updates the retailer's total.

| Detail | Value |
| :---- | :---- |
| **Endpoint** | POST /transaction |

**Sample Request Body:**

{  
    "retailerId": 1,  
    "amountSpent": 175.50,  
    "transactionDate": "2025-10-07"  
}

**Sample Response (200 OK):**

{  
    "retailerId": 1,  
    "name": "Test Retailer",  
    "rewardPoint": 200  
}

### **2\. GET: Retrieve Reward Points (Consolidated)**

Retrieves the monthly or total reward points for a specific retailer using a query parameter.

| Detail | URL Format | Example |
| :---- | :---- | :---- |
| **Total** | /rewardPoints/{id} (default) | /rewardPoints/1 |
| **Monthly** | /rewardPoints/{id}?type=monthly | /rewardPoints/1?type=monthly |

**Sample Response (200 OK):** 90 (Integer value)

### **3\. GET: Three-Month Summary**

Retrieves the aggregated summary of points and transactions for all retailers over the last three months.

| Detail | Value |
| :---- | :---- |
| **Endpoint** | GET /summary |

**Sample Response (200 OK \- Abbreviated):**

\[  
    {  
        "retailer": { "retailerId": 1, "name": "Test Retailer" },  
        "monthlyPoints": { "2025-08": 50, "2025-09": 150 },  
        "totalPoints": 200,  
        "transactions": \[ { /\* ... list of transactions ... \*/ } \]  
    }  
\]

## **Error Model**

All API validation and business rule errors return a structured JSON response body.

| HTTP Status | Description |
| :---- | :---- |
| 400 Bad Request | Validation failed (e.g., missing fields in POST) or a business rule was violated (e.g., retailer.not.found). |
| 500 Internal Server Error | Unhandled server error. |

**Sample Error Response (400 Bad Request):**

{  
    "errorMessage": "Retailer ID is required.",  
    "errorCode": 400  
}  
