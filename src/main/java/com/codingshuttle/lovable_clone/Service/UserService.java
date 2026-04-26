package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
