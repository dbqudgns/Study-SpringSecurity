package jwt_practice.springjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt_practice.springjwt.entity.RefreshEntity;
import jwt_practice.springjwt.repository.RefreshRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

//커스텀 로그인 필터 : /login 경로로 오는 POST 요청을 검증한다. 따라서, 컨토롤러에서 처리할 필요가 없다 !
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    //UsernamePasswordAuthenticationFilter의 역할 : 로그인 한 사용자를 검증하기 위해 username, password 도출 후 토큰 생성
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //1. 클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //2. 스프링 시큐리티에서 username과 password를 검증하기 위해서는 Token에 담아야 한다.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        //3. Token에 담긴 정보를 검증하기 위해 AuthenticationManager로 전달 -> 이후 AuthenticationManager가 CustomUserDetailService의 loadUserByUsername을 호출하여 DB 기반 유저 검증 실시 !!
        return authenticationManager.authenticate(authToken);

    }

    //로그인 성공 시 실행하는 메서드 (이곳에서 JWT를 발급한다.)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        //authentication : Spring Security에서 로그인한 사용자의 인증 정보를 담고 있는 객체
        //인증된 사용자의 정보를 가져온다.

        //유저 정보
        String username = authentication.getName();

        //GrantedAuthority : 사용자가 어떤 권한을 가지고 있는지 정의하는 인터페이스
        //현재 사용자의 권한을 가져온다.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();//첫 번째 권한을 꺼낸다.

        //usernaem, role 정보를 포함한 Access 토큰, Refresh 토큰 생성
        String access = jwtUtil.createJWT("access", username, role, 600000L); //Access 토큰 : 10분
        String refresh = jwtUtil.createJWT("refresh", username, role, 86400000L); //Refresh 토큰 : 24시간

        //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);

        //응답 설정 : 2개의 토큰을 헤더 및 쿠키에 설정
        response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

    }

    //쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(24*60*60); //쿠키 유효 기간을 24시간으로 설정

        //cookie.setSecure(true); HTTPS에서만 쿠키를 전송하도록 설정

        cookie.setPath("/"); //해당 도메인의 모든 경로("/")에서 쿠키를 사용 가능
        //기본적으로 setPath("/")를 설정하지 않아도 쿠키는 해당 도메인의 모든 경로에서 유효함

        cookie.setHttpOnly(false); //자바스크립트에서 쿠키에 접근할 수 없음, XSS 공격을 방지하는 중요한 보안 설정

        return cookie;
    }

    //Refresh 토큰을 저장할 객체인 RefreshEntity 생성 메서드
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setRefresh(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
    //로그인 실패 시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);

    }
}
