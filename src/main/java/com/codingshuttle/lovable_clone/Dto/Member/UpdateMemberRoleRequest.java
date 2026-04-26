package com.codingshuttle.lovable_clone.Dto.Member;

import com.codingshuttle.lovable_clone.Entity.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull ProjectRole role
) {
}
