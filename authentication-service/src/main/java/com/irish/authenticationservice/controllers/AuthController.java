package com.irish.authenticationservice.controllers;

import com.irish.authenticationservice.entities.LoginRequest;
import com.irish.authenticationservice.entities.RegisterRequest;
import com.irish.authenticationservice.entities.AuthResponse;
import com.irish.authenticationservice.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> Login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        AuthResponse authResponse = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }
}
