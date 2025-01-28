package com.irish.authenticationservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenValidationService {
    private final JwtUtil jwtUtil;

    public boolean validateToken(String token) {
        return !jwtUtil.isExpired(token);
    }
}
