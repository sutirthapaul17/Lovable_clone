package com.codingshuttle.lovable_clone.Dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Email String userName,
        @Size(min=4,max=50) String password
) {
}
