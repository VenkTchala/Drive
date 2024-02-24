package com.example.service;

import com.example.Mapper.UserMapper;
import com.example.dto.RegisterUserDto;
import com.example.entity.DriveUser;
import com.example.repository.DriveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DriveUserRepository driveUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final UserMapper userMapper;

    public void saveUser(RegisterUserDto registerUserDto) {
        if(driveUserRepository.existsByEmail(registerUserDto.getEmail()))
            throw new IllegalArgumentException("user with email : " + registerUserDto.getEmail() + "exists");

        DriveUser user = userMapper.newUser(registerUserDto);
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        driveUserRepository.save(user);
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


}
