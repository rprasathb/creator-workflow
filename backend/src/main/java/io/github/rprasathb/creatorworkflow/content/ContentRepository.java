package io.github.rprasathb.creatorworkflow.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<ContentItem, UUID>, JpaSpecificationExecutor<ContentItem> {}
