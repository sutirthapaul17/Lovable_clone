package com.codingshuttle.lovable_clone.Dto.Project;

import com.codingshuttle.lovable_clone.Dto.Auth.UserProfileResponse;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse owner
) {
}
