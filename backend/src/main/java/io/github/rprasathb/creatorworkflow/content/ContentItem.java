package io.github.rprasathb.creatorworkflow.content;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content_items")
public class ContentItem {
    @Id public UUID id;
    @Column(nullable = false, length = 160) public String title;
    @Column(columnDefinition = "text") public String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) public Platform platform;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) public ContentStatus status;
    public Instant scheduledAt;
    public Instant publishedAt;
    @Column(columnDefinition = "text") public String publishedUrl;
    @Column(nullable = false, updatable = false) public Instant createdAt;
    @Column(nullable = false) public Instant updatedAt;

    @PrePersist
    void created() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = createdAt;
    }
    @PreUpdate
    void updated() { updatedAt = Instant.now(); }
}
