# RideLink Account Service

Java Spring Boot microservice for account registration, authentication, role management,
profile management, and account-status control in the IT3130 RideLink group assignment.

## Student ownership

- Student: **Nimnaka K.A.P.**
- Student ID: **IT24102459**
- Assigned service: **Account Service**
- Default port: **8081**
- Owned database: **ridelink_accounts**

This service is independently executable and never reads or modifies another
microservice's database.

## Implemented functions

- Passenger and driver self-registration
- BCrypt password hashing
- Login and signed JWT issuance
- `PASSENGER`, `DRIVER`, and `ADMIN` roles
- Authenticated profile viewing and updating
- Self/admin account retrieval
- Administrator-only account status management
- `ACTIVE`, `SUSPENDED`, and `DISABLED` account states
- Validation and consistent JSON error responses
- Swagger/OpenAPI documentation with Bearer authentication
- Optional administrator bootstrap through environment variables
- Unit tests for normal, boundary, and failure behavior

## Technology stack

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data MongoDB
- Spring Security
- JWT using JJWT
- Jakarta Validation
- Springdoc OpenAPI
- JUnit 5, Mockito, and AssertJ
- Maven Wrapper

## Prerequisites

1. JDK 17 or later
2. MongoDB running locally or a MongoDB Atlas connection
3. Internet access on the first Maven build so dependencies can be downloaded

## Environment configuration

The application intentionally does not contain a real JWT secret or database password.

Required:

| Variable | Description |
|---|---|
| `JWT_SECRET` | Base64-encoded secret containing at least 32 random bytes |

Optional:

| Variable | Default |
|---|---|
| `ACCOUNT_DB_URI` | `mongodb://localhost:27017/ridelink_accounts` |
| `JWT_EXPIRATION_MS` | `3600000` |
| `SERVER_PORT` | `8081` |
| `ADMIN_EMAIL` | Empty; admin bootstrap disabled |
| `ADMIN_PASSWORD` | Empty; admin bootstrap disabled |
| `ADMIN_NAME` | `RideLink Administrator` |

Generate a JWT secret in PowerShell:

```powershell
[Convert]::ToBase64String([byte[]](1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

Set the variables in PowerShell:

```powershell
$env:ACCOUNT_DB_URI="mongodb://localhost:27017/ridelink_accounts"
$env:JWT_SECRET="PASTE_THE_GENERATED_BASE64_VALUE"
$env:ADMIN_EMAIL="admin@ridelink.test"
$env:ADMIN_PASSWORD="AdminPassword@123"
$env:ADMIN_NAME="RideLink Administrator"
```

Generate and export a JWT secret on Bash:

```bash
export JWT_SECRET="$(openssl rand -base64 48)"
export ACCOUNT_DB_URI="mongodb://localhost:27017/ridelink_accounts"
```

Do not commit these values. The included `.env.example` contains names and placeholders only.

## Build and run

Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Open Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```

## API summary

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register passenger or driver |
| POST | `/api/auth/login` | Public | Login and receive a JWT |
| GET | `/api/accounts/me` | Authenticated | View own profile |
| PUT | `/api/accounts/me` | Authenticated | Update own profile |
| GET | `/api/accounts/{accountId}` | Self or admin | Retrieve an account |
| PATCH | `/api/accounts/{accountId}/status` | Admin | Change account status |
| PATCH | `/api/accounts/{accountId}/role` | Admin | Change account role |

Public registration rejects the `ADMIN` role. Configure the optional environment-based
administrator bootstrap to create a demonstration admin safely.

## Example workflow

Register a passenger:

```json
{
  "fullName": "Kasun Perera",
  "email": "kasun@example.com",
  "password": "Password@123",
  "phoneNumber": "0771234567",
  "role": "PASSENGER"
}
```

Login:

```json
{
  "email": "kasun@example.com",
  "password": "Password@123"
}
```

Copy the returned `accessToken`. In Swagger, select **Authorize** and paste only the
token. In Postman, use Bearer Token authentication.

## Postman

Import both files from the `postman` directory:

- `RideLink_Account_Service.postman_collection.json`
- `RideLink_Local.postman_environment.json`

The login request automatically saves `accessToken` and `accountId` into collection
variables. To test the administrator endpoint, login with the bootstrapped admin and
copy its token to the `adminToken` collection variable.

## Tests

Run:

```bash
./mvnw test
```

The tests cover:

- Successful passenger registration
- Email normalization and BCrypt usage
- Duplicate-email rejection
- Rejection of public administrator registration
- Profile updating
- Account suspension
- Unknown account handling
- Login and JWT response creation
- JWT claims and account matching
- Invalid DTO fields

The unit tests do not require a running MongoDB instance because repository behavior is mocked.

## Group repository integration

Place this directory at the group repository root as:

```text
ridelink-backend/account-service/
```

Recommended branch:

```text
feature/account-service-nimnaka
```

The JWT signing secret must be supplied to every service that validates Account Service
tokens. Sharing the signing configuration does not mean sharing a database. Other services
must use the `accountId` JWT subject as the stable cross-service identifier.

## Suggested commit sequence

1. `Create Account Service project structure`
2. `Implement passenger and driver registration`
3. `Add JWT login and Spring Security configuration`
4. `Implement profile and account status APIs`
5. `Add validation and global exception handling`
6. `Add Account Service unit tests and Swagger documentation`
7. `Add Postman scenarios and setup documentation`

Do not upload the entire project as one unexplained commit. Commit work as it is reviewed
and understood, then create a pull request to the group's integration branch.

## Academic integrity

Review and understand every class before submission. The assignment brief requires any
permitted generative-AI use to be declared. A suggested factual declaration is included in
`docs/AI_USE_DECLARATION.txt`; edit it so that it accurately describes the group's real use.
