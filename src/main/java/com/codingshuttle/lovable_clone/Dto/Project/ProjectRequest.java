package com.codingshuttle.lovable_clone.Dto.Project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank String name
) {
}
