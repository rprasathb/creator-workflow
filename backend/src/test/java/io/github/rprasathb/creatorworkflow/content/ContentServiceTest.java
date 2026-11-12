package io.github.rprasathb.creatorworkflow.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {
    @Mock ContentRepository contents;
    @Mock MetricsRepository metrics;
    ContentService service;
    UUID id;

    @BeforeEach
    void setup() { service = new ContentService(contents, metrics); id = UUID.randomUUID(); }

    private ContentItem item(ContentStatus status) {
        ContentItem c = new ContentItem();
        c.id = id;
        c.title = "First short";
        c.platform = Platform.TIKTOK;
        c.status = status;
        c.createdAt = Instant.parse("2026-01-01T00:00:00Z");
        c.updatedAt = c.createdAt;
        return c;
    }

    @Test
    void scheduledContentNeedsTimestamp() {
        var request = new ContentDto.Request("test", null, Platform.TIKTOK,
            ContentStatus.SCHEDULED, null, null, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));
        assertEquals(400, ex.getStatusCode().value());
        verifyNoInteractions(contents);
    }

    @Test
    void publishedContentNeedsTimestamp() {
        var request = new ContentDto.Request("test", null, Platform.TIKTOK,
            ContentStatus.PUBLISHED, null, null, null);
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.create(request)).getStatusCode().value());
    }

    @Test
    void missingContentReturns404() {
        when(contents.findById(id)).thenReturn(Optional.empty());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.get(id)).getStatusCode().value());
    }

    @Test
    void metricsRequirePublishedContent() {
        when(contents.findById(id)).thenReturn(Optional.of(item(ContentStatus.DRAFT)));
        var payload = new ContentDto.MetricsRequest(10L, 1L, 0L, 0L);
        assertEquals(409, assertThrows(ResponseStatusException.class,
            () -> service.upsertMetrics(id, payload)).getStatusCode().value());
        verifyNoInteractions(metrics);
    }

    @Test
    void upsertMetricsRecordsCounts() {
        ContentItem content = item(ContentStatus.PUBLISHED);
        when(contents.findById(id)).thenReturn(Optional.of(content));
        when(metrics.findByContentId(id)).thenReturn(Optional.empty());
        when(metrics.saveAndFlush(any(ContentMetrics.class))).thenAnswer(call -> call.getArgument(0));
        var saved = service.upsertMetrics(id, new ContentDto.MetricsRequest(100L, 7L, 2L, 1L));
        assertEquals(100, saved.views());
        assertEquals(7, saved.likes());
        assertNotNull(saved.recordedAt());
    }

    @Test
    void archivedContentCannotBeEditedWithoutRestore() {
        when(contents.findById(id)).thenReturn(Optional.of(item(ContentStatus.ARCHIVED)));
        var request = new ContentDto.Request("edited", null, Platform.TIKTOK,
            ContentStatus.DRAFT, null, null, null);
        assertEquals(409, assertThrows(ResponseStatusException.class,
            () -> service.update(id, request)).getStatusCode().value());
    }

    @Test
    void restoreMovesArchivedToIdea() {
        ContentItem content = item(ContentStatus.ARCHIVED);
        when(contents.findById(id)).thenReturn(Optional.of(content));
        when(contents.saveAndFlush(any(ContentItem.class))).thenAnswer(call -> call.getArgument(0));
        assertEquals(ContentStatus.IDEA, service.restore(id).status());
    }
}
