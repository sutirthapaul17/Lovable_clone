package com.codingshuttle.lovable_clone.Dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapping;

public record SignupRequest(
        @NotBlank @Email String userName,
        @Size(min=1,max=30) String name,
        @Size(min = 4) String password
) {
}
