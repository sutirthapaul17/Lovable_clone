package com.codingshuttle.lovable_clone.Dto.Project;

import java.time.Instant;

public record ProjectSummeryResponse(
        Long id,
        String projectName,
        Instant createdAt,
        Instant updatedAt
) {
}
