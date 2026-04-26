package com.codingshuttle.lovable_clone.Dto.Member;

import com.codingshuttle.lovable_clone.Entity.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
        @Email @NotBlank String userName,
        @NotNull ProjectRole role
) {
}
