package io.github.rprasathb.creatorworkflow.dashboard;

import io.github.rprasathb.creatorworkflow.content.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final ContentRepository contents;
    private final MetricsRepository metrics;
    public DashboardController(ContentRepository contents, MetricsRepository metrics) {
        this.contents = contents;
        this.metrics = metrics;
    }
    public record Summary(Map<ContentStatus, Long> byStatus, long totalViews, long totalLikes, long publishedCount) {}
    @GetMapping("/summary")
    public Summary summary() {
        Map<ContentStatus, Long> counts = new EnumMap<>(ContentStatus.class);
        for (ContentStatus status : ContentStatus.values()) counts.put(status, 0L);
        for (ContentItem item : contents.findAll()) counts.merge(item.status, 1L, Long::sum);
        return new Summary(counts, metrics.totalViews(), metrics.totalLikes(), counts.get(ContentStatus.PUBLISHED));
    }
    @GetMapping("/upcoming")
    public java.util.List<ContentDto.Response> upcoming() {
        var spec = (org.springframework.data.jpa.domain.Specification<ContentItem>) (root, query, cb) ->
            cb.and(cb.equal(root.get("status"), ContentStatus.SCHEDULED),
                cb.greaterThan(root.<Instant>get("scheduledAt"), Instant.now()));
        return contents.findAll(spec, Sort.by("scheduledAt").ascending()).stream()
            .limit(50).map(ContentDto.Response::from).toList();
    }
}
