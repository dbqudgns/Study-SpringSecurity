package com.Spring.OAuthJWT.dto;

import com.Spring.OAuthJWT.entity.Role;
import lombok.Builder;

@Builder
public record UserDTO(

        String username,
        String name,
        String email,
        Role role

) {}
