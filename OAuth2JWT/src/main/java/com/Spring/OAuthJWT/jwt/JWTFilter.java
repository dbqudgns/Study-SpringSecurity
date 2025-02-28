package com.Spring.OAuthJWT.jwt;

import com.Spring.OAuthJWT.dto.CustomOAuth2User;
import com.Spring.OAuthJWT.dto.UserDTO;
import com.Spring.OAuthJWT.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//로그인한 사용자의 JWT를 이용한 인증을 처리하는 필터
//요청이 들어올 때마다 실행되며, 사용자 인증을 수행하는 역할
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //재로그인 무한 루프 오류를 해결하기 위한 로직
        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/login(?:\\/.*)?$")) { // /login/oauth2/code/서비스명

            filterChain.doFilter(request, response);
            return;
        }
        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) { // /oauth2/authorization/서비스명

            filterChain.doFilter(request, response);
            return;
        }


        String access = null;
        access = request.getHeader("Authorization");


        if (access == null) {
            filterChain.doFilter(request, response);
            return;
        } else {
            access = access.replace("Bearer ", "");
        }



        try {
            jwtUtil.isExpired(access);
        }
        catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(access);

        if(!category.equals("access")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);
        Role userRole = Role.valueOf(role.replace("ROLE_", ""));

        //UserDTO를 생성하여 값 set
        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .role(userRole)
                .build();

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 전용 인증 토큰 생성 : 인증된 사용자 객체, 비밀번호, 사용자 권한 목록
        //비밀번호 null인 이유 : JWT는 이미 서버에서 발급한 토큰이므로 별도로 비밀번호 검증을 하지 않는다.
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록 : 이후 컨트롤러에서 @AuthenticationPrincipal 등을 사용해 현재 로그인한 사용자를 가져올 수 있다.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
