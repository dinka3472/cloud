package com.irish.authenticationservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jwt_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long id;

    @Column(unique = true)
    public String token;

    @Column
    public boolean revoked;

    @Column
    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "users_jwt_tokens")
    public User user;

}
