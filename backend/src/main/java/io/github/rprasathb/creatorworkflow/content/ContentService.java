package io.github.rprasathb.creatorworkflow.content;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;

@Service
public class ContentService {
    private final ContentRepository contents;
    private final MetricsRepository metrics;

    public ContentService(ContentRepository contents, MetricsRepository metrics) {
        this.contents = contents;
        this.metrics = metrics;
    }

    @Transactional(readOnly = true)
    public Page<ContentDto.Response> list(Platform platform, ContentStatus status, String search, Pageable pageable) {
        Specification<ContentItem> spec = (root, query, cb) -> cb.conjunction();
        if (platform != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("platform"), platform));
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if (search != null && !search.isBlank()) {
            String q = "%" + search.strip().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), q));
        }
        return contents.findAll(spec, pageable).map(ContentDto.Response::from);
    }

    @Transactional(readOnly = true)
    public ContentDto.Response get(UUID id) { return ContentDto.Response.from(require(id)); }

    private ContentItem require(UUID id) {
        return contents.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
    }

    @Transactional
    public ContentDto.Response create(ContentDto.Request request) {
        ContentItem item = new ContentItem();
        apply(item, request);
        return ContentDto.Response.from(contents.saveAndFlush(item));
    }

    @Transactional
    public ContentDto.Response update(UUID id, ContentDto.Request request) {
        ContentItem item = require(id);
        if (item.status == ContentStatus.ARCHIVED && request.status() != ContentStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Archived content must be restored explicitly");
        }
        apply(item, request);
        return ContentDto.Response.from(contents.saveAndFlush(item));
    }

    private void apply(ContentItem item, ContentDto.Request r) {
        if (r.status() == ContentStatus.SCHEDULED && r.scheduledAt() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledAt is required for SCHEDULED");
        if (r.status() == ContentStatus.PUBLISHED && r.publishedAt() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "publishedAt is required for PUBLISHED");
        item.title = r.title().strip();
        item.description = r.description();
        item.platform = r.platform();
        item.status = r.status();
        item.scheduledAt = r.scheduledAt();
        item.publishedAt = r.publishedAt();
        item.publishedUrl = r.publishedUrl();
    }

    @Transactional
    public void delete(UUID id) {
        ContentItem item = require(id);
        metrics.findByContentId(id).ifPresent(metrics::delete);
        contents.delete(item);
    }

    @Transactional
    public ContentDto.MetricsResponse upsertMetrics(UUID id, ContentDto.MetricsRequest request) {
        ContentItem item = require(id);
        if (item.status != ContentStatus.PUBLISHED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Metrics require PUBLISHED content");
        ContentMetrics metric = metrics.findByContentId(id).orElseGet(() -> {
            ContentMetrics m = new ContentMetrics();
            m.content = item;
            return m;
        });
        metric.views = request.views();
        metric.likes = request.likes();
        metric.comments = request.comments();
        metric.shares = request.shares();
        metric.recordedAt = Instant.now();
        return ContentDto.MetricsResponse.from(metrics.saveAndFlush(metric));
    }

    @Transactional
    public ContentDto.Response restore(UUID id) {
        ContentItem item = require(id);
        if (item.status != ContentStatus.ARCHIVED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only archived content can be restored");
        item.status = ContentStatus.IDEA;
        return ContentDto.Response.from(contents.saveAndFlush(item));
    }
}
