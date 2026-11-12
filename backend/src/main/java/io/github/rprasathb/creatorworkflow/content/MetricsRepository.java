package io.github.rprasathb.creatorworkflow.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface MetricsRepository extends JpaRepository<ContentMetrics, UUID> {
    Optional<ContentMetrics> findByContentId(UUID contentId);
    @Query("select coalesce(sum(m.views), 0) from ContentMetrics m")
    long totalViews();
    @Query("select coalesce(sum(m.likes), 0) from ContentMetrics m")
    long totalLikes();
}
