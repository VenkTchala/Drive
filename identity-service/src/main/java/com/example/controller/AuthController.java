package com.example.controller;

import com.example.dto.AuthRequest;
import com.example.dto.RegisterUserDto;
import com.example.dto.SignInStatus;
import com.example.dto.TokenDto;
import com.example.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public SignInStatus addNewUser(@RequestBody RegisterUserDto registerUserDto) {
        return  authService.saveUser(registerUserDto);
    }

    @PostMapping("/token")
    public TokenDto getToken(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return true;
    }
}
