package io.github.rprasathb.creatorworkflow.content;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ContentControllerTest {
    ContentService service = mock(ContentService.class);
    ContentController controller = new ContentController(service);

    @Test
    void rejectInvalidPagination() {
        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> controller.list(null, null, null, -1, 20));
        assertEquals(400, ex.getStatusCode().value());
        verifyNoInteractions(service);
    }

    @Test
    void createReturnsLocation() {
        UUID id = UUID.randomUUID();
        var request = new ContentDto.Request("Title", null, Platform.TIKTOK, ContentStatus.IDEA, null, null, null);
        var response = new ContentDto.Response(id, "Title", null, Platform.TIKTOK,
            ContentStatus.IDEA, null, null, null, Instant.now(), Instant.now());
        when(service.create(request)).thenReturn(response);
        ResponseEntity<ContentDto.Response> result = controller.create(request);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("/api/v1/content/" + id, result.getHeaders().getLocation().toString());
    }
}
