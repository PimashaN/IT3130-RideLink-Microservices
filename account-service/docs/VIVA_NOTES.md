# Nimnaka K.A.P. - Account Service Viva Notes

## What my service owns

My Account Service owns registration, login, password hashes, roles, basic profile data,
and account status. Driver operational details belong to the Driver & Vehicle Service.
Ride and payment records belong to their respective services.

## Why JWT

After login, the Account Service issues a signed JWT. Other services validate the token
locally, so they do not call the Account Service for every request. This reduces coupling
and supports independent scaling. Tokens are stateless, so immediate revocation needs an
additional mechanism; a short expiry reduces that limitation for this assignment.

## Security decisions

- Passwords are hashed with BCrypt and are never returned by an API.
- Public registration permits only passenger or driver roles.
- Admin creation uses environment variables rather than a public endpoint.
- Method security protects administrator operations.
- Secrets and database credentials are supplied through environment variables.
- Validation rejects malformed input before it reaches business logic.

## Important classes to explain

- `SecurityConfig`: endpoint rules, stateless sessions, BCrypt, authentication provider.
- `JwtAuthenticationFilter`: reads Bearer token and populates the security context.
- `JwtService`: signs, parses, and validates JWTs.
- `AccountService`: registration, duplicate checks, profile updates, and status changes.
- `AuthService`: authentication and token response creation.
- `GlobalExceptionHandler`: consistent API errors.

## Negative demonstrations

1. Register the same email twice and show `409 Conflict`.
2. Call `/api/accounts/me` without a token and show `401 Unauthorized`.
3. Use a passenger token on the status endpoint and show `403 Forbidden`.
4. Register with invalid values and show field-level `400 Bad Request` errors.
5. Suspend an account using an admin token, then show that a new login is rejected.

## Likely questions

**Why separate databases?** Each microservice owns its persistence boundary and can evolve
independently. Direct cross-service database access would tightly couple the services.

**Why use DTOs?** DTOs prevent exposing password hashes and separate the API contract from
the database document.

**Why normalize email?** Lowercasing and trimming prevents duplicate logical identities
such as `USER@example.com` and `user@example.com`.

**How do other services identify the user?** The stable account ID is stored in the JWT
subject and used as a cross-service reference.

**What would you improve?** Add refresh tokens, email verification, password reset,
key rotation with asymmetric signing, audit events, rate limiting, and integration tests
using Testcontainers.
