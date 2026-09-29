# Inventory Manager App

A full-stack inventory and asset-circulation system designed for tracking organizational equipment, finding patrons, and recording checkouts and returns. 

The system is split into two separate applications: a React UI in the `frontend/` directory and a Spring Boot API in the `backend/` directory.

## Technology Stack

**Backend:**
* Java 21
* Spring Boot 4.1 
* PostgreSQL
* Redis (Caching)
* Flyway (Database Migrations)
* JJWT (JSON Web Tokens)

**Frontend:**
* React 19
* Vite
* Tailwind CSS 4

## Core Workflows

* **Dashboard:** Displays counts of total, available, checked-out, and damaged assets. It provides separate views for the asset catalog and patron lookup, paginating both lists in groups of 10.
* **Asset Catalog:** Searchable by asset tag, name, brand, and description. Assets maintain statuses (AVAILABLE, UNAVAILABLE, CHECKED_OUT, RESERVED, DAMAGED, REPAIRING) and utilize a version field for stale-update detection.
* **Patron Lookup:** Searchable by user email or full name. Displays the patron's system role, contact details, and any active circulation loans.
* **Checkout & Returns:** Staff can check out an available asset to a borrower with a specific due date. Returns record item condition and optional notes. Damaged returns move the asset to DAMAGED; intact items return to AVAILABLE.

## Architecture & API Features

* **API & Roles:** Versioned under `/api/v1`. Roles (`USER`, `EMPLOYEE`, `ADMIN`) are enforced via API authorization rules.
* **Authentication:** Spring Security utilizing JWT. Access tokens expire in 15 minutes, while refresh tokens are handled securely via an HTTP-only cookie lasting seven days.
* **Concurrency & Safety:** Checkout operations obtain a pessimistic write lock to prevent simultaneous checkouts of the same asset. Asset modifications utilize optimistic locking. 
* **Audit Logging:** Create, update, checkout, and check-in actions publish asynchronous audit events that are recorded after the transaction commits.
* **Caching:** Redis acts as the Spring cache provider. Individual asset lookups have a 10-minute cache TTL, with targeted eviction on asset changes.

## Local Development Setup

### Prerequisites
* Java 21
* Node.js & npm
* Docker & Docker Compose

### Environment Variables
Avoid committing actual credentials. Create a .env file in the backend directory or pass these into your environment:

**Backend Requirements:**
* `DB_URL` (e.g., `jdbc:postgresql://localhost:5432/inventory_db`)
* `DB_USERNAME`
* `DB_PASSWORD`
* `JWT_SECRET`
* `REDIS_HOST` (defaults to `localhost`)
* `CORS_ALLOWED_ORIGINS` (defaults to `http://localhost:5173`)

**Optional Backend Account Startup Seeds:**
* `APP_SEED_ADMIN_EMAIL` / `APP_SEED_ADMIN_PASSWORD`
* `APP_SEED_EMPLOYEE_EMAIL` / `APP_SEED_EMPLOYEE_PASSWORD`

**Frontend Override:**
* `VITE_API_BASE_URL` (defaults to `http://localhost:8080/api/v1`)

### Run Commands

**1. Start Backend Dependencies (PostgreSQL & Redis):**
From the `backend/` directory:
```bash
docker compose up -d
```
(Note: Docker Compose expects POSTGRES_USER, POSTGRES_PASSWORD, and POSTGRES_DB to be set in your environment, this can just match your backend DB username and password).

**2. Start the Backend API:**
From the `backend/` directory:
```bash
./mvnw spring-boot:run
```

**3. Start the Frontend Application:**
From the `frontend/` directory:
```bash
npm run dev
```

## Testing & Build

* **Backend Tests:** Run `./mvnw test`. Note that integration tests utilize Testcontainers for PostgreSQL, so Docker must be running on your machine.
* **Frontend Build:** Production builds are generated using `npm run build`. 

## Deployment

The backend includes a `Dockerfile` that builds a Java 21 application image and exposes port 8080 for deployment. The provided `docker-compose.yml` is just for the local database and caching. The actual frontend/backend start up is not handled by docker.
