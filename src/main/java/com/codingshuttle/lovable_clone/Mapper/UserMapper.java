package com.codingshuttle.lovable_clone.Mapper;


import com.codingshuttle.lovable_clone.Dto.Auth.SignupRequest;
import com.codingshuttle.lovable_clone.Dto.Auth.UserProfileResponse;
import com.codingshuttle.lovable_clone.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source ="userName",target="username" )
    User toEntity(SignupRequest request);

    @Mapping(source ="username",target="userName" )
    UserProfileResponse toUserProfileResponse(User user);
}
