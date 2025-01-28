package com.irish.authenticationservice.controllers;

import com.irish.authenticationservice.services.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/validate")
public class TokenValidationController {

    private final TokenValidationService tokenValidationService;


    @GetMapping
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token is missing or empty");
        }

        boolean isValid = tokenValidationService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }
}
