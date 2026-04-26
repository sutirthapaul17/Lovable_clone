package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Auth.AuthResponse;
import com.codingshuttle.lovable_clone.Dto.Auth.LoginRequest;
import com.codingshuttle.lovable_clone.Dto.Auth.SignupRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    AuthResponse signUp(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
