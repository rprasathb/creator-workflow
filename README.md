# CreatorWorkflow

A content planning and performance tracking platform for short-form creators using TikTok, YouTube Shorts and Instagram Reels.

## MVP
Organise content ideas, track drafts through publication, plan posting dates, record manual engagement metrics and view a lightweight dashboard.

**Scheduling in this MVP means planning a publish date; it does not automatically publish content.** No platform API connections are required in the first release.

## Project status
Architecture and MVP scope are defined. Application code has not been implemented yet.

## Planning documents
- [Architecture, data model and REST endpoints](docs/architecture.md)
- [24-hour MVP implementation plan](docs/implementation-plan.md)
- [Draft API examples](docs/api-examples.md)

## Proposed stack
Java 21, Spring Boot 3, Angular, PostgreSQL, Flyway and Docker Compose.

## Development approach
Small reviewed pull requests, automated tests, no committed secrets. The MVP is designed for local, single-user use; authentication and deployment hardening are future work.
