# CreatorWorkflow frontend

Angular 20 standalone application for the CreatorWorkflow MVP. Provides an overview dashboard, content library, filters, content editor, manual scheduling and published-content metrics.

## Requirements
- Node.js 22 LTS and npm
- Running CreatorWorkflow Spring Boot backend at http://localhost:8080

## Run locally
From the repository root:

```bash
docker compose up -d postgres
cd backend
mvn spring-boot:run
```

In a **second terminal**:

```bash
cd frontend
npm install
npm start
```

Open http://localhost:4200. Angular's development proxy forwards `/api` requests to the backend. The browser therefore doesn't need a production CORS configuration for local development.

## Build
```bash
cd frontend
npm install
npm run build
```

The GitHub Actions workflow builds the frontend on PRs.

## MVP usage
1. Select **New content** to create an idea.
2. Change its status to draft/ready/scheduled; scheduled status requires a planned date.
3. Mark it published and provide its actual publish date.
4. Use **Metrics** on published items to enter views, likes, comments and shares.
5. See upcoming scheduled items and status counts on the Overview screen.

## Important limitations
- This is a local **single-user** MVP with no login; do not expose it on the internet.
- Scheduling is only a planned date. The app **does not** publish to TikTok, Instagram or YouTube.
- Performance metrics are manual. The backend currently only supports saving, not retrieving per-item metrics; reopening Metrics starts with zeros and saving again **overwrites** the previous values. Retrieving existing values is a future backend improvement.
- An archived item must be restored before editing.
- No image uploads, posting automation or external OAuth are included.
