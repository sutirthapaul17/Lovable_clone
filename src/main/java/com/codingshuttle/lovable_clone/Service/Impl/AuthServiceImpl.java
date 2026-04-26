package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Auth.AuthResponse;
import com.codingshuttle.lovable_clone.Dto.Auth.LoginRequest;
import com.codingshuttle.lovable_clone.Dto.Auth.SignupRequest;
import com.codingshuttle.lovable_clone.Entity.User;
import com.codingshuttle.lovable_clone.Mapper.UserMapper;
import com.codingshuttle.lovable_clone.Repository.UserRepository;
import com.codingshuttle.lovable_clone.Service.AuthService;
import com.codingshuttle.lovable_clone.error.BadRequestException;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signUp(SignupRequest request) {
        userRepository.findByUsername(request.userName()).ifPresent(
                user -> {throw new BadRequestException("User Already exists by username: "+request.userName());}
        );
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        String token = authUtil.generateAccessToken(user);


        return new AuthResponse(token,userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.userName(),request.password()
                )
        );
        User user = (User) authentication.getPrincipal();
        assert user != null;
        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token,userMapper.toUserProfileResponse(user));
    }
}
