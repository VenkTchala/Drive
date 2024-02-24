package com.example.controller;

import com.example.dto.AuthRequest;
import com.example.dto.RegisterUserDto;
import com.example.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public void addNewUser(@RequestBody RegisterUserDto registerUserDto) {
        service.saveUser(registerUserDto);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return service.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return true;
    }
}
