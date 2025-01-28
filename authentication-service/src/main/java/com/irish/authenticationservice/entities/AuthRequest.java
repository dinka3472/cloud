package com.irish.authenticationservice.entities;

import lombok.Data;

@Data
public class AuthRequest {
    private String accessToken;
    private String refreshToken;
    public AuthRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
