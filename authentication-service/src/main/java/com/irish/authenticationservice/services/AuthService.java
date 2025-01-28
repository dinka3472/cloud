package com.irish.authenticationservice.services;

import com.irish.authenticationservice.entities.*;
import com.irish.authenticationservice.exception.InvalidTokenException;
import com.irish.authenticationservice.repository.RoleRepository;
import com.irish.authenticationservice.repository.TokenRepository;
import com.irish.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Set<Role> roleSet = new HashSet<>();
        Role role = Role.builder().name(RoleNameEnum.ROLE_USER).build();
        roleRepository.save(role);
        roleSet.add(role);
       // roleSet.add(roleRepository.getRoleByName(RoleNameEnum.ROLE_USER));
        user.setRoles(roleSet);
        var savedUser = userRepository.save(user);

        var jwtToken = jwtUtil.generate(user, TokenType.ACCESS);
        var refreshToken = jwtUtil.generate(user,TokenType.REFRESH);
        saveUserToken(savedUser, refreshToken);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail());

        var jwtToken = jwtUtil.generate(user, TokenType.ACCESS);
        var refreshToken = jwtUtil.generate(user,TokenType.REFRESH);

        saveUserToken(user, refreshToken);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    public AuthResponse refreshAccessToken(String refreshToken) {

        if (jwtUtil.isExpired(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        RefreshToken storedToken = tokenRepository.findByToken(refreshToken)
                 .orElseThrow(() -> new InvalidTokenException("Refresh token not found or revoked"));

        User user = storedToken.getUser();
        var newAccessToken = jwtUtil.generate(user, TokenType.ACCESS);
        var newRefreshToken = jwtUtil.generate(user,TokenType.REFRESH);

        storedToken.setToken(newRefreshToken);
        tokenRepository.save(storedToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = RefreshToken.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
