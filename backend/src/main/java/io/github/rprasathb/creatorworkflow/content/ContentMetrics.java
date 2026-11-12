package io.github.rprasathb.creatorworkflow.content;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content_metrics")
public class ContentMetrics {
    @Id public UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    public ContentItem content;
    @Column(nullable = false) public long views;
    @Column(nullable = false) public long likes;
    @Column(nullable = false) public long comments;
    @Column(nullable = false) public long shares;
    @Column(nullable = false) public Instant recordedAt;

    @PrePersist
    void created() { if (id == null) id = UUID.randomUUID(); }
}
