package com.codingshuttle.lovable_clone.Dto.Auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
