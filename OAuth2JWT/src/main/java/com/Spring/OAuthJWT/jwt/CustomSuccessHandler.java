package com.Spring.OAuthJWT.jwt;

import com.Spring.OAuthJWT.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

//로그인 성공 후 JWT 발급 클래스 : 쿠키로 프론트단에게 JWT를 보냄
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, 60*60*60L);

        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://localhost:3000/"); //클라이언트가 응답을 받으면 해당 URL로 재요청한다. -> Main Page 띄울 때 쓰자
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60); //쿠키 유효 기간을 24시간으로 설정
        //cookie.setSecure(true); //HTTPS에서만 쿠키를 전송하도록 설정 (개발환경에서만 설정)
        cookie.setPath("/"); //해당 도메인의 모든 경로("/")에서 쿠키를 사용 가능
        cookie.setHttpOnly(true); //자바스크립트에서 쿠키에 접근할 수 없음, xss 공격을 방지하는 중요한 보안 설정 !

        return cookie;

    }


}
