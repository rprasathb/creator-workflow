# CreatorWorkflow — 24-hour MVP Plan

Goal: a demonstrable, locally runnable web application for content planning and manually entered creator analytics. 24 hours is a planning estimate, not a guarantee.

## Acceptance criteria
- User can create, update, delete and search/filter content by status and platform.
- User can schedule a planned publish date (without automatic social posting).
- User can mark an item published and enter/update non-negative metrics.
- Dashboard shows correct status counts, upcoming scheduled content and aggregate views.
- Invalid payloads and unknown IDs produce tested, clear HTTP errors.
- `docker compose up` starts required local services with documented setup.
- README includes screenshots or a short recorded demo when UI exists.

## Agent tasks and order

### Task 1 — Backend foundation (4 h)
- Scaffold Java 21 Spring Boot backend: Web, Validation, JPA, PostgreSQL, Flyway, Actuator.
- Add entity + migration for content_items and content_metrics.
- Add repository, DTO mapping, validation and status transition service.
- Implement CRUD and query endpoint with pagination.
- Unit/integration tests for validation and persistence.
**Done when:** backend tests pass and CRUD operates against a test database.

### Task 2 — API and dashboard (3 h)
- Add metrics upsert and published-state validation.
- Add dashboard summary/upcoming endpoints.
- Provide example requests and documented response shapes.
- Test aggregation queries and edge cases.
**Done when:** seeded content yields expected dashboard data.

### Task 3 — Frontend foundation (4 h)
- Scaffold Angular standalone app with navigation, API client and typed models.
- Build content list, filters and item form.
- Provide loading, empty and error states.
**Done when:** an item can be created and edited through the UI.

### Task 4 — Scheduling and analytics UI (3 h)
- Add schedule-date controls and published status handling.
- Add metrics input for published items and dashboard cards.
- Confirm local-time display while backend stores UTC.
**Done when:** full planning-to-published workflow works end to end.

### Task 5 — Local integration (3 h)
- Add PostgreSQL Docker Compose, environment sample and seed strategy.
- Connect frontend/backend via documented local configuration and CORS.
- Verify clean startup from fresh checkout.
**Done when:** documented startup works without manually creating tables.

### Task 6 — Quality and delivery (5 h)
- Integration and regression tests, UI smoke test, input validation.
- Fix priority defects; add CI workflow for backend tests and frontend build.
- README with setup, architecture, scope and limitations.
**Done when:** CI checks pass and smoke test is reproducible.

### Task 7 — Review buffer (2 h)
- Review agent-generated code, simplify complexity, address defects.
- Prepare a 60–90-second demonstration script.
**Done when:** MVP is ready to present as a portfolio project.

## Working conventions
- Small feature branches and pull requests; do not push directly to main.
- Run tests and report actual results, not assumed success.
- Keep tasks within the MVP scope. No automatic publishing or platform integrations yet.
- Avoid adding Kafka, Redis, Kubernetes, complex microservices or AI solely for technology showcase.
- Keep secrets out of commits and avoid storing platform access tokens for MVP.

## Future work (not in 24 h)
Multi-platform content grouping, authentication and accounts, media library, social-platform OAuth, native analytics sync, scheduling/publishing via authorised APIs, reminders, advanced charting and AI-assisted captions.
