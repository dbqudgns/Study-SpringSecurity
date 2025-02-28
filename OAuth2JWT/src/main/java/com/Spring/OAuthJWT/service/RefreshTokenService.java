package com.Spring.OAuthJWT.service;

import com.Spring.OAuthJWT.entity.RefreshEntity;
import com.Spring.OAuthJWT.repository.RefreshRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshRepository refreshRepository;

    @Transactional
    public void saveRefreshToken(String username, Integer expireS, String refresh) {

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(new Date(System.currentTimeMillis() + expireS * 1000).toString())
                .build();

        refreshRepository.save(refreshEntity);

    }
}
