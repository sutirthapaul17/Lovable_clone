package com.codingshuttle.lovable_clone.Dto.Member;

import com.codingshuttle.lovable_clone.Entity.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String userName,
        String name,
//        String avatarUrl,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
