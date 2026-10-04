# IT3130-RideLink

## IT3130 Application Development - RideLink Backend Microservices

### Ride Management Service

The Ride Management Service is responsible for managing the complete ride lifecycle in the RideLink ride-sharing platform.

#### Main Responsibilities

- Create and retrieve ride requests
- Assign available drivers
- Manage ride status lifecycle
- Accept ride requests
- Start rides
- Complete rides
- Cancel rides
- Validate ride status transitions
- Handle invalid ride requests and missing rides
- Communicate with the Driver Service through REST APIs

#### Ride Status Lifecycle

```text
REQUESTED
    ↓
ACCEPTED
    ↓
STARTED
    ↓
COMPLETED

A ride can also be cancelled while it is in REQUESTED or ACCEPTED status.

API Endpoints
Method	Endpoint	Description
POST	/api/rides	Create a new ride
GET	/api/rides	Retrieve all rides
GET	/api/rides/{id}	Retrieve a ride by ID
PUT	/api/rides/{id}/accept	Accept a ride
PUT	/api/rides/{id}/start	Start a ride
PUT	/api/rides/{id}/complete	Complete a ride
PUT	/api/rides/{id}/cancel	Cancel a ride
Technologies
Java
Spring Boot
Spring Data JPA
Spring Security
REST API
H2 Database
Swagger / OpenAPI
Maven
JUnit
Inter-Service Communication

The Ride Management Service communicates with the Driver Service using REST APIs to:

Retrieve an available driver.
Update the assigned driver's availability.
Security

The service uses HTTP Basic Authentication and validates authenticated requests before allowing access to ride management APIs.

Testing

Unit tests and API testing were performed to verify:

Ride creation
Driver assignment
Ride lifecycle transitions
Ride cancellation
Invalid ride status handling
Ride not found scenarios
Authentication
