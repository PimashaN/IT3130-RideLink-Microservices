# Account Service API Contract

## Data ownership

The Account Service owns identity, credentials, roles, profile details, and account status.
It does not own driver availability, vehicles, rides, fares, payments, or receipts.

Other services store only the stable `accountId` needed for their own business records.
They must never connect to the Account Service MongoDB database.

## JWT contract

The Account Service signs JWTs containing:

| Claim | Meaning |
|---|---|
| `sub` | Stable account identifier |
| `email` | Authenticated email address |
| `role` | `PASSENGER`, `DRIVER`, or `ADMIN` |
| `iat` | Token issue time |
| `exp` | Token expiry time |

Downstream services validate the signature and expiry locally using Spring Security.
This avoids a synchronous Account Service call on every request. A limitation is that an
already-issued token may remain usable until expiry after account suspension; the one-hour
expiry limits this window. A future production system could use short-lived access tokens,
refresh-token revocation, or an identity provider.

## HTTP behavior

- `201 Created`: successful registration
- `200 OK`: successful login, profile retrieval, or update
- `400 Bad Request`: validation or prohibited public admin registration
- `401 Unauthorized`: missing/invalid token or invalid credentials
- `403 Forbidden`: valid identity without sufficient role/ownership
- `404 Not Found`: unknown account identifier
- `409 Conflict`: duplicate email address

## Example error

```json
{
  "timestamp": "2026-09-27T12:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Email address is already registered",
  "path": "/api/auth/register",
  "validationErrors": {}
}
```
