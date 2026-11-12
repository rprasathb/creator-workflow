# CreatorWorkflow — Initial REST Contract (Draft)

Base URL: `http://localhost:8080/api/v1`

All examples are design targets for the implementation tasks, not live endpoints yet.

## Create content
`POST /content`

```json
{
  "title": "3 mistakes when starting short-form content",
  "description": "Hook, example and CTA",
  "platform": "YOUTUBE_SHORTS",
  "status": "IDEA"
}
```

Expected: `201 Created` with generated UUID, timestamps and fields.

## List/filter
`GET /content?status=IDEA&platform=YOUTUBE_SHORTS&page=0&size=20`

Expected: paginated items with total count.

## Schedule
`PUT /content/{id}`

```json
{
  "title": "3 mistakes when starting short-form content",
  "description": "Hook, example and CTA",
  "platform": "YOUTUBE_SHORTS",
  "status": "SCHEDULED",
  "scheduledAt": "2026-11-15T18:00:00Z"
}
```

## Mark as published
`PUT /content/{id}`

```json
{
  "title": "3 mistakes when starting short-form content",
  "platform": "YOUTUBE_SHORTS",
  "status": "PUBLISHED",
  "scheduledAt": "2026-11-15T18:00:00Z",
  "publishedAt": "2026-11-15T18:03:00Z",
  "publishedUrl": "https://example.com/video/123"
}
```

## Record metrics
`PUT /content/{id}/metrics`

```json
{
  "views": 240,
  "likes": 12,
  "comments": 3,
  "shares": 2
}
```

Expected: `200 OK` with updated metrics and recorded timestamp; `409 Conflict` if content is not published.

## Dashboard
`GET /dashboard/summary`

Example response:
```json
{
  "byStatus": {"IDEA": 4, "DRAFT": 2, "READY": 1, "SCHEDULED": 3, "PUBLISHED": 5, "ARCHIVED": 0},
  "totalViews": 10500,
  "totalLikes": 450,
  "publishedCount": 5
}
```

`GET /dashboard/upcoming`

Expected: list of items in status SCHEDULED with scheduledAt in the future, ascending.

## Error format
```json
{
  "code": "VALIDATION_ERROR",
  "message": "scheduledAt is required when status is SCHEDULED",
  "details": ["scheduledAt"]
}
```

These shapes can be refined when implementation begins, with API contract tests to lock in the final behavior.
