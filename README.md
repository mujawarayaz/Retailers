
# Customer Reward Points API

This is a Spring Boot application designed to calculate reward points for customer transactions over a three-month period based on a retailer's loyalty program.




## Reward Calculation Rules

Reward points are calculated based on the whole dollar amount spent in a single transaction:

    1) 2 points for every dollar spent over $100.
    2) 1 point for every dollar spent between $50 and $100.
    3) 0 points for amounts under $50.
Example: -
A $120.50 Transaction
Points from the $50-$100 tier: ($100 - $50) * 1 = 50 points
Points from the over-$100 tier: ($120 - $100) \* 2 = 40 points\
Total Points: 90

## Prerequisites
   1) Java 17+
   2) Maven
   3) A running MySQL instance

## Database Configuration

| Property | Default Value     | Description                |
| :-------- | :------- | :------------------------- |
| DB_USERNAME | `root` | Database username. |

## Database Schema

The complete SQL script to create the required tables and seed initial data is located in the project at:

```
src/main/resources/TableScript.sql
```
## How to Run

1) Clone the repository.
2) Navigate to the project's root directory.
3) Run the application using the Maven wrapper: 
```
Bash
mvn spring-boot:run
```
4) The application will start on
```
 http://localhost:8765.
```
## API Reference

The base URL for the application is 
```
http://localhost:8765/retail.
```
#### Record a Transaction :

This endpoint records a new transaction for a customer and a retailer. The response confirms the creation and includes the server-generated transactionId and the calculated rewardPoint.

##### POST:    
```
http://localhost:8765/retail/transactions
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| Request Body | Json | **Required**. Transaction Dto |

##### Request Body

```
{
    "customerId": 101,
    "retailerId": 101,
    "amountSpent": 120.50,
    "transactionDate": "2025-10-08"
}
```
##### Sample Response

```
{
    "transactionId": 2,
    "retailerId": 101,
    "customerId": 101,
    "amountSpent": 120.5,
    "rewardPoint": 90,
    "transactionDate": "2025-10-08"
}
```

#### Get Customer Rewards :

This single endpoint retrieves reward information for a specific customer. An optional view parameter controls the level of detail in the response.

##### GET: 
```
http://localhost:8765/retail/rewards/customers/{customerId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `customerId`      | `string` | **Required**. Id of item to fetch |

1. Get Full 3-Month Summary (Default View) 
``` 
http://localhost:8765/retail/rewards/customers/101
```
 Response (200 OK):
```
{
    "customerId": 101,
    "customerName": "Ayaz Mujawar",
    "totalRewardPoints": 180,
    "monthlyPoints": {
        "October": 180
    }
}
```
2. Get Total Points Only 
``` 
http://localhost:8765/retail/rewards/customers/101?view=total
```
 Response (200 OK):
```
{
    "customerId": 101,
    "points": 180
}
```
3. Get Last Month's Points Only
``` 
http://localhost:8765/retail/rewards/customers/101?view=monthly
```
 Response (200 OK):
```
{
    "customerId": 101,
    "points": 180
}
```
### Error Handling

Validation and business rule errors return a 400 Bad Request status with a structured JSON response.

#### Sample Error Response:

```
{
    "errorMessage": "Customer not found with ID: 999",
    "errorCode": 400
}
```


