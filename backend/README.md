# CreatorWorkflow backend

Java 21 / Spring Boot 3 REST API. **Local single-user MVP only**; no authentication, so do not expose this server to the public internet.

## Start
1. Install Java 21, Maven and Docker.
2. From the repository root: `docker compose up -d postgres`.
3. From `backend/`: `mvn spring-boot:run`.
4. Health: `http://localhost:8080/actuator/health`.
5. API base: `http://localhost:8080/api/v1`.

Defaults are intentionally development-only. Set DB_USER, DB_PASSWORD, DB_URL for your environment. Backend and Compose must use matching credentials.

## Test
Run `mvn test` from `backend/`.

## Endpoints
- GET/POST /api/v1/content
- GET/PUT/DELETE /api/v1/content/{id}
- POST /api/v1/content/{id}/restore
- PUT /api/v1/content/{id}/metrics
- GET /api/v1/dashboard/summary
- GET /api/v1/dashboard/upcoming

See [draft API examples](../docs/api-examples.md).
