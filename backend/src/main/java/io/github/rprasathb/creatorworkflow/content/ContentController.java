package io.github.rprasathb.creatorworkflow.content;

import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ContentService service;
    public ContentController(ContentService service) { this.service = service; }

    @GetMapping
    public Page<ContentDto.Response> list(@RequestParam(required = false) Platform platform,
        @RequestParam(required = false) ContentStatus status,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100)
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,
                "page must be nonnegative and size must be between 1 and 100");
        return service.list(platform, status, search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @GetMapping("/{id}")
    public ContentDto.Response get(@PathVariable UUID id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<ContentDto.Response> create(@Valid @RequestBody ContentDto.Request request) {
        ContentDto.Response result = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/content/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    public ContentDto.Response update(@PathVariable UUID id, @Valid @RequestBody ContentDto.Request request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }

    @PostMapping("/{id}/restore")
    public ContentDto.Response restore(@PathVariable UUID id) { return service.restore(id); }

    @PutMapping("/{id}/metrics")
    public ContentDto.MetricsResponse metrics(@PathVariable UUID id,
        @Valid @RequestBody ContentDto.MetricsRequest request) {
        return service.upsertMetrics(id, request);
    }
}
