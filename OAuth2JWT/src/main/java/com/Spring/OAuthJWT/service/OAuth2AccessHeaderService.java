package com.Spring.OAuthJWT.service;

import com.Spring.OAuthJWT.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuth2AccessHeaderService {

    @Transactional
    public ResponseEntity<?> oauth2AccessHeader(HttpServletRequest request, HttpServletResponse response) {

        Cookie [] cookies = request.getCookies();
        String access = null;

        if (cookies == null) {
            throw new IllegalArgumentException("쿠키가 없습니다.");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                access = cookie.getValue();
            }
        }

        if (access == null) {
            throw new IllegalArgumentException("쿠키에 Access 토큰이 없습니다. key 값이 Authorization 인지 확인해주세요.");
        }

        //클라이언트의 쿠키 중 Access 토큰을 없앰
        response.addCookie(CookieUtil.createCookie("Authorization", null, 0));
        response.setHeader("Authorization", "Bearer " + access);

        return ResponseEntity.ok(Map.of("message", "Access 토큰 Header 반환 완료 및 해당 쿠키 삭제"));

    }

}
