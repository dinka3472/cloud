package com.irish.authenticationservice.services;
import com.irish.authenticationservice.entities.Role;
import com.irish.authenticationservice.entities.TokenType;

import com.irish.authenticationservice.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expiration;

    private Key key;

    @PostConstruct
    public void initKey() {
      // this.key =  Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    public String generate(User user, TokenType tokenType) {
        //Map<String, String> claims = Map.of("id", userId, "role", role);
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("roles", getRoleNames(user.getRoles().stream().toList()));

        long expMillis = TokenType.ACCESS.equals(tokenType)
                ? Long.parseLong(expiration) * 1000
                : Long.parseLong(expiration) * 1000 * 5;

        final Date now = new Date();
        final Date exp = new Date(now.getTime() + expMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                //.signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    private List<String> getRoleNames(List<Role> userRoles) {
        List<String> result = new ArrayList<>();
        userRoles.forEach(role -> result.add(role.getName().name()));
        return result;
    }
}
