package com.Spring.OAuthJWT.service;

import com.Spring.OAuthJWT.jwt.JWTUtil;
import com.Spring.OAuthJWT.repository.RefreshRepository;
import com.Spring.OAuthJWT.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

//Refresh 토큰을 인증하여 Access 토큰 재발급 하는 서비스 또한 Refresh Rotate(화이트리스트)
@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshRepository refreshRepository;

    @Transactional
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {

            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }

        }

        if (refresh == null) {
            throw new IllegalArgumentException("쿠키에 Refresh 토큰이 없습니다.");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Refresh 토큰이 만료됐습니다. 다시 로그인 해주세요.");
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            throw new IllegalArgumentException("Refresh 토큰이 아닙니다.");
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);

        if(!isExist) {
            throw new IllegalArgumentException("저장소에 해당 Refresh 토큰이 없습니다.");
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //새로운 Access, Refresh 토큰 발급 (Refresh Rotate)
        Integer expiredS = 60 * 60 * 24;
        String newAccess = jwtUtil.createJwt("access", username, role, 60 * 10 * 1000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, expiredS * 1000L);

        // 기존 Refresh 토큰 DB에서 삭제 후 새로운 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        refreshTokenService.saveRefreshToken(username, expiredS, refresh);

        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(CookieUtil.createCookie("refresh", newRefresh, expiredS));

        return ResponseEntity.ok(Map.of("message", "Access 토큰, Refresh 토큰 재발급 완료"));

    }


}
