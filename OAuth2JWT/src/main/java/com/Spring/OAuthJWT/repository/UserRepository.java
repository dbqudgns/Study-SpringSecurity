package com.Spring.OAuthJWT.repository;

import com.Spring.OAuthJWT.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

}
