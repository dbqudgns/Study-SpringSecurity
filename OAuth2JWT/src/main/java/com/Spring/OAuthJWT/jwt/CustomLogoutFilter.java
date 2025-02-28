package com.Spring.OAuthJWT.jwt;

import com.Spring.OAuthJWT.repository.RefreshRepository;
import com.Spring.OAuthJWT.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //경로명 확인
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        //http Method 확인
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        //쿠키를 통해 Refresh 토큰을 얻는다.
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);

        if(!isExist) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteByRefresh(refresh);

        //Refresh 토큰 전용 Cookie 값 null로 설정
        Cookie RefreshCookie = CookieUtil.createCookie("refresh", null, 0);
        response.addCookie(RefreshCookie);

        response.setStatus(HttpServletResponse.SC_OK);

    }


}
