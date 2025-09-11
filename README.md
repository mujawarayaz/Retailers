#  Customer Reward Points System

This is a Spring Boot backend application that calculates **reward points** for customers based on their **transaction history**. Customers earn points according to the following rules:

- **2 points** for every dollar spent **over $100** in a single transaction.
- **1 point** for every dollar spent **between $50 and $100**.
- **0 points** for amounts under $50.

The application processes all customer transactions over a **three-month period**, calculating:
- **Monthly reward points**
- **Total reward points** per customer

---

# Features

- Spring Boot backend application  
- Maven build system  
- REST API support  
- Uses Java 17  

---

# Reward Points Rules

# Transactions Over $100:
- For every dollar spent **above $100**, earn **2 points per dollar**

#Transactions Between $50 and $100:
- For every dollar spent in this range, earn **1 point per dollar**

---

# Example Calculation

For a transaction of **$120**:

- First $50 → **0 points**
- Next $50 (from $50 to $100) → **50 points**
- Final $20 (over $100) → **2 points/dollar × 20 = 40 points**

**Total = 90 points**

---

# Access Point
- API Access -> http://localhost:8765/retail
- POST -> /transaction
- GET -> /transactionPerMonth/{retailerId}
- GET -> /totalReward/{retailerId}

# Prerequisites

- Java 17 installed  
- Maven installed (optional; you can use the included Maven Wrapper)
- MySQL database running

---



