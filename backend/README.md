# Gita server (REST API)

Spring Boot 3 service that mirrors the Android app’s Room entities: users, items, trades (with offered/requested item links). Authentication uses **JWT** in the `Authorization: Bearer <token>` header. Passwords are stored with **BCrypt**.

## Run locally

Requirements: **JDK 17**. The project uses **Gradle** (wrapper included).

**Windows:**

```bat
cd backend
gradlew.bat bootRun
```

**macOS / Linux:**

```bash
cd backend
chmod +x gradlew   # first time only
./gradlew bootRun
```

Other useful tasks: `./gradlew build`, `./gradlew test`.

Defaults: port **8080**, H2 database files under `backend/data/` (created on first run).

### Why do I see `{"error":"Unauthorized"}`?

Almost every API path requires a **JWT**. Only these are public without a token: `GET /`, `GET /api/health`, and `POST /api/auth/register` / `POST /api/auth/login`.

1. Register or log in:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{"username":"alice","password":"secret"}
```

2. Copy `token` from the JSON response.

3. Call protected routes with:

```http
Authorization: Bearer <paste token here>
```

Example (PowerShell):

```powershell
$body = '{"username":"alice","password":"secret"}'
$r = Invoke-RestMethod -Uri http://localhost:8080/api/auth/login -Method POST -Body $body -ContentType 'application/json'
$h = @{ Authorization = "Bearer $($r.token)" }
Invoke-RestMethod -Uri http://localhost:8080/api/users/me -Headers $h
```

## Configuration

| Env / property | Purpose |
|----------------|---------|
| `GITA_JWT_SECRET` | HMAC key for JWT signing (use a long random string in production). |
| `gita.jwt.expiration-ms` | Token lifetime (default 7 days). |

Override in `src/main/resources/application.yml` or via env-specific config.

## API overview

| Method | Path | Auth |
|--------|------|------|
| POST | `/api/auth/register` | No |
| POST | `/api/auth/login` | No |
| GET | `/api/users/me` | Yes |
| PATCH | `/api/users/me` | Yes |
| GET | `/api/items/mine` | Yes |
| GET | `/api/items/marketplace?includeNonTrade=&query=` | Yes |
| GET | `/api/items/inventory/stats` | Yes |
| GET | `/api/items/{id}` | Yes |
| POST | `/api/items` | Yes |
| PUT | `/api/items/{id}` | Yes |
| DELETE | `/api/items/{id}` | Yes |
| POST | `/api/trades` | Yes |
| GET | `/api/trades/incoming` | Yes |
| GET | `/api/trades/outgoing` | Yes |
| POST | `/api/trades/{id}/accept` | Yes |
| POST | `/api/trades/{id}/reject` | Yes |

JSON field names align with the app where it matters (e.g. `isLookingToTrade` on items).

Error responses use `{ "error": "message" }` with appropriate HTTP status codes.

## Android emulator

If the app runs in an emulator, use base URL `http://10.0.2.2:8080` to reach the host machine’s localhost.
