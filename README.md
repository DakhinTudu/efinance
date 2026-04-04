# Finance Dashboard Backend

A **Spring Boot** REST API for managing financial records with role-based access control, JWT authentication, and dashboard analytics.

## Tech Stack

| Layer          | Technology                      |
|----------------|---------------------------------|
| Framework      | Spring Boot 3.3.6 (Java 21)    |
| Database       | H2 (in-memory)                  |
| Security       | Spring Security + JWT (JJWT)    |
| Hashing        | BCrypt                          |
| Validation     | Jakarta Bean Validation         |
| Documentation  | SpringDoc OpenAPI (Swagger UI)  |
| Build          | Maven                           |
| Containerizing | Docker + Docker Compose         |

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker (optional)

### Run Locally

```bash
# Clone and navigate
git clone <repo-url>
cd Zorvyn

# Copy environment template and edit if needed
cp .env.example .env

# Build and run
./mvnw spring-boot:run
```

### Run with Docker

```bash
docker compose up --build
```

### Access Points

| Resource       | URL                                    |
|----------------|----------------------------------------|
| API Base       | `http://localhost:8080/api/v1`          |
| Swagger UI     | `http://localhost:8080/swagger-ui.html` |
| H2 Console     | `http://localhost:8080/h2-console`      |
| Health Check   | `http://localhost:8080/actuator/health` |

> **H2 Console**: JDBC URL = `jdbc:h2:mem:financedb`, User = `sa`, Password = (empty)

---

## Default Admin Credentials

| Field    | Value              |
|----------|--------------------|
| Email    | admin@efinace.com  |
| Password | Admin@123          |

Configured via `.env` file — **never hardcoded**.

---

## API Endpoints

### Authentication (Public)

| Method | Endpoint                 | Description        |
|--------|--------------------------|--------------------|
| POST   | `/api/v1/auth/login`     | Login, returns JWT |
| POST   | `/api/v1/auth/register`  | Register new user  |

### Users (MANAGE_USERS — Admin only)

| Method | Endpoint              | Description       |
|--------|-----------------------|-------------------|
| GET    | `/api/v1/users`       | List all users    |
| GET    | `/api/v1/users/{id}`  | Get user by ID    |
| PUT    | `/api/v1/users/{id}`  | Update user       |
| DELETE | `/api/v1/users/{id}`  | Deactivate user   |

### Financial Records

| Method | Endpoint               | Permission     | Description          |
|--------|------------------------|----------------|----------------------|
| POST   | `/api/v1/records`      | WRITE_RECORDS  | Create record        |
| GET    | `/api/v1/records`      | READ_RECORDS   | List (paginated)     |
| GET    | `/api/v1/records/{id}` | READ_RECORDS   | Get by ID            |
| PUT    | `/api/v1/records/{id}` | WRITE_RECORDS  | Update record        |
| DELETE | `/api/v1/records/{id}` | DELETE_RECORDS | Soft-delete record   |

**Filters**: `type`, `categoryId`, `startDate`, `endDate`, `page`, `size`, `sortBy`, `sortDir`

### Dashboard (VIEW_ANALYTICS — Analyst + Admin)

| Method | Endpoint                          | Description    |
|--------|-----------------------------------|----------------|
| GET    | `/api/v1/dashboard/summary`       | Full summary   |
| GET    | `/api/v1/dashboard/trends/monthly`| Monthly trends |

### Categories

| Method | Endpoint                   | Permission        | Description    |
|--------|----------------------------|-------------------|----------------|
| GET    | `/api/v1/categories`       | READ_RECORDS      | List all       |
| POST   | `/api/v1/categories`       | MANAGE_CATEGORIES | Create         |
| PUT    | `/api/v1/categories/{id}`  | MANAGE_CATEGORIES | Update         |
| DELETE | `/api/v1/categories/{id}`  | MANAGE_CATEGORIES | Delete         |

---

## Roles & Permissions

| Permission         | VIEWER | ANALYST | ADMIN |
|--------------------|--------|---------|-------|
| READ_RECORDS       | ✅     | ✅      | ✅    |
| WRITE_RECORDS      | ❌     | ❌      | ✅    |
| DELETE_RECORDS     | ❌     | ❌      | ✅    |
| VIEW_ANALYTICS     | ❌     | ✅      | ✅    |
| MANAGE_USERS       | ❌     | ❌      | ✅    |
| MANAGE_CATEGORIES  | ❌     | ❌      | ✅    |

---

## Architecture

```
com.efinace/
├── config/          Security, Swagger, DataInitializer
├── controller/      REST endpoints (5 controllers)
├── service/         Business logic (5 interfaces + 5 impls)
├── repository/      JPA data access (5 repositories)
├── entity/          JPA entities (5 entities)
├── dto/             Request/Response DTOs + ApiResponse
├── mapper/          Entity ↔ DTO mappers
├── security/        JWT filter, util, CustomUserDetails
├── exception/       Global exception handler + custom exceptions
├── enums/           RoleName, PermissionName, RecordType, UserStatus
└── util/            ApiResponseBuilder, SecurityUtils
```

### Design Decisions

- **H2 in-memory** — zero setup, ideal for evaluation; schema initialized via `db.sql`
- **ddl-auto=none** — schema managed by SQL script, not Hibernate auto-generation
- **Soft delete** — financial records are never physically removed
- **Permission-based auth** — `@PreAuthorize("hasAuthority('...')")` for fine-grained control
- **Structured responses** — all endpoints return `ApiResponse<T>` with consistent shape
- **Env-driven secrets** — JWT secret and admin credentials from `.env`, never committed

---

## Database Choice

**H2 (in-memory)** was chosen because:
- Zero external setup required for evaluators
- Schema auto-initializes from `db.sql` on each startup
- H2 Console provides a web UI for inspecting data
- Easily swappable to PostgreSQL/MySQL by changing `application.properties`

---

## Environment Variables

| Variable           | Description                    | Default              |
|--------------------|--------------------------------|----------------------|
| `JWT_SECRET`       | Base64-encoded HMAC key        | (dev key in .env)    |
| `JWT_EXPIRATION_MS`| Token expiry in milliseconds   | 86400000 (24h)       |
| `SERVER_PORT`      | Application port               | 8080                 |
| `ADMIN_EMAIL`      | Default admin email            | admin@efinace.com    |
| `ADMIN_PASSWORD`   | Default admin password         | Admin@123            |
| `H2_CONSOLE_ENABLED`| Enable H2 web console        | true                 |
