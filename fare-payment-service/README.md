# RideLink — Fare & Payment Service

## 1. Service Overview

The **Fare & Payment Service** is a Spring Boot microservice developed for the RideLink ride-hailing system.

The service is responsible for:

* Calculating and storing ride fares
* Retrieving fares by fare ID or ride ID
* Initiating payments
* Confirming and failing simulated payments
* Preventing duplicate successful payments for the same ride
* Generating receipts after successful payments
* Providing REST APIs for fare, payment, and receipt operations
* Validating API inputs and returning structured error responses

The service is designed to operate independently from the Ride and Account services. It stores stable identifiers such as `rideId` and `payerAccountId` rather than creating direct JPA relationships with entities owned by other microservices.

---

## 2. Owner / Student Information

| Field        | Details                |
| ------------ | ---------------------- |
| Student Name | Adhikari A.Y.S         |
| Student ID   | IT24102544             |
| Service      | Fare & Payment Service |
| Project      | RideLink               |
| Module       | IT3130                 |

---

## 3. Technology Stack

* **Java:** 17
* **Spring Boot:** 4.1.1
* **Maven:** Maven Wrapper
* **Spring Web:** REST API development
* **Spring Data MongoDB:** Database persistence
* **Jakarta Validation:** Request and path parameter validation

---

## 4. Service Configuration

| Configuration     | Value                           |
| ----------------- | ------------------------------- |
| Service Name      | `fare-payment-service`          |
| Port              | `8084`                          |
| Database          | MongoDB (local)                 |
| Database Name     | `ridelink_payments`             |
| Connection URI    | `mongodb://localhost:27017/ridelink_payments` |
| GUI Client        | MongoDB Compass (`mongodb://localhost:27017`) |

The service can be accessed locally at:

```text
http://localhost:8084
```

The service information endpoint is:

```text
GET http://localhost:8084/
```

Example response:

```json
{
  "service": "fare-payment-service",
  "status": "UP",
  "port": 8084,
  "database": "MongoDB",
  "api": {
    "fares": "/api/fares",
    "payments": "/api/payments",
    "receipts": "/api/receipts"
  }
}
```

---

# 5. Project Structure

```text
fare-payment-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── lk/
│   │   │       └── sliit/
│   │   │           └── ridelink/
│   │   │               └── fare/
│   │   │                   ├── controller/
│   │   │                   ├── dto/
│   │   │                   ├── entity/
│   │   │                   ├── exception/
│   │   │                   ├── repository/
│   │   │                   └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── ...
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# 6. Domain Model

The service contains three main persisted entities.

### Fare

Stores the calculated fare for a ride.

Important information includes:

* Fare ID
* Ride ID
* Passenger ID
* Driver ID
* Distance
* Duration
* Base fare
* Per-kilometre rate
* Per-minute rate
* Surge multiplier
* Total amount
* Currency
* Creation timestamp

### Payment

Stores payment information associated with a fare/ride.

Important information includes:

* Payment ID
* Fare ID
* Ride ID
* Payer account ID
* Amount
* Payment method
* Payment status
* Transaction reference
* Payment timestamp

### Receipt

Stores receipt information generated after a successful payment.

Important information includes:

* Receipt ID
* Receipt number
* Payment ID
* Issue timestamp
* Amount
* Ride ID
* Payment method

---

# 7. Payment Methods

The supported payment methods are:

```text
CASH
CARD
WALLET
```

Payments are simulated for this project. No real payment gateway is connected.

---

# 8. Payment Statuses

The service supports the following payment statuses:

```text
PENDING
SUCCESS
FAILED
REFUNDED
```

The normal demonstration flow is:

```text
PENDING
   ↓
SUCCESS
```

or:

```text
PENDING
   ↓
FAILED
```

Cash payments can initially remain `PENDING` and then be confirmed as `SUCCESS`.

---

# 9. Fare Calculation

The fare calculation uses the configured fare rates.

The general calculation is:

```text
total fare =
(base fare
 + distance × per-kilometre rate
 + duration × per-minute rate)
 × surge multiplier
```

Fare rates are configured through application properties rather than being hardcoded directly into the controller.

The service validates that required numerical inputs such as distance and duration are greater than zero.

A second fare cannot be created for the same `rideId`.

---

# 10. REST API Endpoints

## Fare Endpoints

### Calculate Fare

```text
POST /api/fares/calculate
```

Calculates and stores a fare.

Example request:

```json
{
  "rideId": 101,
  "passengerId": "P001",
  "driverId": "D001",
  "distanceKm": 10.0,
  "durationMinutes": 20
}
```

Successful response:

```text
HTTP 201 Created
```

---

### Get Fare by ID

```text
GET /api/fares/{id}
```

Example:

```text
GET /api/fares/1
```

Returns the fare associated with the specified fare ID.

---

### Get Fare by Ride ID

```text
GET /api/fares/ride/{rideId}
```

Example:

```text
GET /api/fares/ride/101
```

Returns the fare associated with the specified ride.

---

# 11. Payment Endpoints

### Initiate Payment

```text
POST /api/payments
```

Creates a new payment.

Example request:

```json
{
  "rideId": 101,
  "payerAccountId": "P001",
  "amount": 400.00,
  "method": "CARD"
}
```

The payment is initially created with `PENDING` status.

Successful response:

```text
HTTP 201 Created
```

---

### Confirm Payment

```text
POST /api/payments/{id}/confirm
```

Confirms a pending payment.

Example:

```text
POST /api/payments/1/confirm
```

The payment status changes from:

```text
PENDING → SUCCESS
```

A receipt is generated after successful payment.

---

### Fail Payment

```text
POST /api/payments/{id}/fail
```

Marks a pending payment as failed.

Example:

```text
POST /api/payments/2/fail
```

The payment status changes to:

```text
FAILED
```

---

### Get Payment by ID

```text
GET /api/payments/{id}
```

Example:

```text
GET /api/payments/1
```

Returns the specified payment.

---

### Get Payments by Ride ID

```text
GET /api/payments/ride/{rideId}
```

Example:

```text
GET /api/payments/ride/101
```

Returns the payments associated with a ride.

---

# 12. Receipt Endpoint

### Get Receipt by Payment ID

```text
GET /api/receipts/{paymentId}
```

Example:

```text
GET /api/receipts/1
```

A receipt is available after the associated payment has successfully reached:

```text
SUCCESS
```

---

# 13. Validation and Error Handling

The service includes centralized error handling using `GlobalExceptionHandler`.

Invalid requests return structured JSON responses rather than exposing raw server errors.

Validation includes:

* Positive fare IDs
* Positive payment IDs
* Positive ride IDs
* Valid request body fields
* Valid payment methods
* Positive fare inputs
* Duplicate fare prevention
* Payment amount matching the fare
* Duplicate successful payment prevention
* Non-existent resource handling

Typical HTTP status codes include:

| Status                      | Meaning                               |
| --------------------------- | ------------------------------------- |
| `200 OK`                    | Successful retrieval/update           |
| `201 Created`               | Fare/payment successfully created     |
| `400 Bad Request`           | Invalid request or validation failure |
| `404 Not Found`             | Requested resource does not exist     |
| `409 Conflict`              | Duplicate/conflicting operation       |
| `500 Internal Server Error` | Unexpected server error               |

Example validation response:

```json
{
  "timestamp": "2026-09-29T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/fares/0",
  "validationErrors": {
    "...": "fare id must be greater than 0"
  }
}
```

---

# 14. Database

The service uses a local MongoDB server during development.

Connection URI:

```text
mongodb://localhost:27017/ridelink_payments
```

Collections include:

```text
fares
payments
receipts
```

MongoDB persists data on disk, so records survive application restarts. To reset, drop the collections (or the whole database) from MongoDB Compass or `mongosh`.

Unique indexes (created at startup by `MongoIndexConfig`):

```text
fares.rideId
payments.transactionRef (sparse)
receipts.receiptNumber
```

---

# 15. How to Run

## Prerequisites

Make sure Java 17 is installed and available.

Verify Java:

```bash
java -version
```

Verify the Java compiler:

```bash
javac -version
```

The project includes the Maven Wrapper, so Maven does not need to be installed separately.

---

## Start the Service

Open a terminal in the `fare-payment-service` directory and run:

### Windows

```bash
.\mvnw.cmd spring-boot:run
```

The application should start on:

```text
http://localhost:8084
```

---

## Build the Project

To compile the project:

```bash
.\mvnw.cmd clean compile
```

---

# 16. API Testing with Postman

The REST API was tested using Postman.

The main successful demonstration flow is:

### Step 1 — Calculate Fare

```text
POST /api/fares/calculate
```

### Step 2 — Create Payment

```text
POST /api/payments
```

### Step 3 — Confirm Payment

```text
POST /api/payments/{id}/confirm
```

### Step 4 — Retrieve Receipt

```text
GET /api/receipts/{paymentId}
```

Additional endpoints tested include:

```text
GET /api/fares/{id}
GET /api/fares/ride/{rideId}
GET /api/payments/{id}
GET /api/payments/ride/{rideId}
POST /api/payments/{id}/fail
```

---

# 17. Negative / Validation Testing

The service was also tested with invalid operations.

Examples include:

### Invalid ID

```text
GET /api/fares/0
```

Expected:

```text
400 Bad Request
```

### Invalid distance

A fare request with:

```text
distanceKm = 0
```

is rejected.

### Invalid duration

A fare request with:

```text
durationMinutes = 0
```

is rejected.

### Duplicate Fare

Attempting to create another fare for an existing `rideId` is rejected with a conflict response.

### Incorrect Payment Amount

A payment whose amount does not match the fare amount is rejected.

### Duplicate Successful Payment

A second successful payment for the same ride is rejected.

These tests verify both API validation and business-rule enforcement.

---

# 18. Ride Service Integration

The Fare & Payment Service currently uses `rideId` as a stable identifier and does not create a direct JPA relationship with the Ride Service.

The client is expected to provide a completed ride ID when calculating or paying for a ride.

A future integration can use a `RideServiceClient` to call:

```text
GET http://localhost:8080/api/rides/{id}
```

and verify that the ride status is:

```text
COMPLETED
```

before allowing fare calculation/payment operations.

This integration is intentionally kept separate from the core fare and payment implementation.

---

# 19. Service Responsibilities

The Fare & Payment Service owns:

* Fare calculation
* Fare persistence
* Payment creation
* Payment status management
* Payment validation
* Receipt generation
* Receipt retrieval

The Ride Service remains responsible for:

* Ride creation
* Ride lifecycle
* Ride status
* Ride-related operations

The Account Service remains responsible for:

* Account management
* User/account information

This separation follows the microservice principle that each service owns its own domain data and business logic.

---

# 20. AI Declaration

AI tools were used as development assistance during the implementation of this service.

AI assistance was used for:

* Understanding Spring Boot and Spring Data JPA concepts
* Structuring the microservice layers
* Generating and reviewing boilerplate code
* Identifying implementation issues
* Reviewing validation and exception-handling approaches
* Assisting with API testing and debugging
* Improving project documentation

The implementation was reviewed, tested, and integrated by the student. The student is responsible for understanding the code and explaining the implementation during the viva/demo.

---

# 21. Current Implementation Status

| Component                 | Status                        |
| ------------------------- | ----------------------------- |
| Spring Boot project       | Completed                     |
| MongoDB (local)           | Completed                     |
| Mongo documents           | Completed                     |
| Repositories              | Completed                     |
| Fare business logic       | Completed                     |
| Payment business logic    | Completed                     |
| DTOs                      | Completed                     |
| REST controllers          | Completed                     |
| Validation                | Completed                     |
| Global exception handling | Completed                     |
| Postman API testing       | Completed                     |
| Receipt generation        | Completed                     |
| Ride Service integration  | Optional / Future enhancement |

---

# 22. Main Demo Flow

The recommended demonstration flow is:

```text
Client / Postman
       │
       ▼
Calculate Fare
POST /api/fares/calculate
       │
       ▼
FareService
       │
       ▼
Fare stored in MongoDB
       │
       ▼
Create Payment
POST /api/payments
       │
       ▼
Payment = PENDING
       │
       ▼
Confirm Payment
POST /api/payments/{id}/confirm
       │
       ▼
Payment = SUCCESS
       │
       ▼
Receipt generated
       │
       ▼
GET /api/receipts/{paymentId}
       │
       ▼
Receipt returned
```

This demonstrates the complete core functionality of the RideLink Fare & Payment Service.
