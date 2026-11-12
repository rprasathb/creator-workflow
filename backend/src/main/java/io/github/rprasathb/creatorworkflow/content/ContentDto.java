package io.github.rprasathb.creatorworkflow.content;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class ContentDto {
    private ContentDto() {}

    public record Request(
        @NotBlank @Size(max = 160) String title,
        String description,
        @NotNull Platform platform,
        @NotNull ContentStatus status,
        Instant scheduledAt,
        Instant publishedAt,
        String publishedUrl
    ) {}

    public record Response(
        UUID id, String title, String description, Platform platform,
        ContentStatus status, Instant scheduledAt, Instant publishedAt,
        String publishedUrl, Instant createdAt, Instant updatedAt
    ) {
        public static Response from(ContentItem c) {
            return new Response(c.id, c.title, c.description, c.platform, c.status,
                c.scheduledAt, c.publishedAt, c.publishedUrl, c.createdAt, c.updatedAt);
        }
    }

    public record MetricsRequest(
        @NotNull @PositiveOrZero Long views,
        @NotNull @PositiveOrZero Long likes,
        @NotNull @PositiveOrZero Long comments,
        @NotNull @PositiveOrZero Long shares
    ) {}

    public record MetricsResponse(long views, long likes, long comments, long shares, Instant recordedAt) {
        public static MetricsResponse from(ContentMetrics m) {
            return new MetricsResponse(m.views, m.likes, m.comments, m.shares, m.recordedAt);
        }
    }
}
