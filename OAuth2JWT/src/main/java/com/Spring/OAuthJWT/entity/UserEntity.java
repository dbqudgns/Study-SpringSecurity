package com.Spring.OAuthJWT.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; //provider + provider id => 식별자

    private String name; //실제 이름

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public UserEntity() {}

    @Builder
    public UserEntity(String username, String email, String name, Role role) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public void updateUserEntity(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }



}
