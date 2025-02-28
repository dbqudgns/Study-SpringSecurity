package com.Spring.OAuthJWT.jwt;

import com.Spring.OAuthJWT.dto.CustomOAuth2User;
import com.Spring.OAuthJWT.service.RefreshTokenService;
import com.Spring.OAuthJWT.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 로그인 성공 후 JWT 발급 클래스 : 쿠키로 프론트단에게 JWT를 보냄
 * 프론트 단에서 하이퍼링크로 OAuth2 로그인을 요청하기 때문에 헤더로 전달 불가능
 * access, refresh -> httpOnly 쿠키 설정
 */
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String name = customUserDetails.getName();
        String username = customUserDetails.getUsername();
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        Integer expireS = 24 * 60 * 60;
        String access = jwtUtil.createJwt("access", username, role, 60 * 10* 1000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, expireS * 1000L);

        //refresh 토큰 DB 저장 (화이트리스트)
        refreshTokenService.saveRefreshToken(username, expireS, refresh);

        response.addCookie(CookieUtil.createCookie("Authorization", access, 60 * 10));
        response.addCookie(CookieUtil.createCookie("refresh", refresh, expireS));

        //access 토큰을 Header로 재발급하기 위한 리디렉트 설정
        String paramName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        response.sendRedirect("http://localhost:3000/change-to-header?name=" + paramName);

    }




}
