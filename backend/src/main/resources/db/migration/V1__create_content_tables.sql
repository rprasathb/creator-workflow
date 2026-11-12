CREATE TABLE content_items (
  id UUID PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  description TEXT,
  platform VARCHAR(32) NOT NULL,
  status VARCHAR(24) NOT NULL,
  scheduled_at TIMESTAMPTZ,
  published_at TIMESTAMPTZ,
  published_url TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_content_status_platform ON content_items(status, platform);
CREATE INDEX idx_content_scheduled ON content_items(scheduled_at);
CREATE TABLE content_metrics (
  id UUID PRIMARY KEY,
  content_id UUID NOT NULL UNIQUE REFERENCES content_items(id) ON DELETE CASCADE,
  views BIGINT NOT NULL DEFAULT 0 CHECK (views >= 0),
  likes BIGINT NOT NULL DEFAULT 0 CHECK (likes >= 0),
  comments BIGINT NOT NULL DEFAULT 0 CHECK (comments >= 0),
  shares BIGINT NOT NULL DEFAULT 0 CHECK (shares >= 0),
  recorded_at TIMESTAMPTZ NOT NULL
);
