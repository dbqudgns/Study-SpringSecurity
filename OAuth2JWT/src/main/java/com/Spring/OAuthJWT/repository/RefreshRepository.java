package com.Spring.OAuthJWT.repository;

import com.Spring.OAuthJWT.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
}
