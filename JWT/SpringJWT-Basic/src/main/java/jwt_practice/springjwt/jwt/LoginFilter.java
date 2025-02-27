package jwt_practice.springjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

//커스텀 로그인 필터 : /login 경로로 오는 POST 요청을 검증한다. 따라서, 컨토롤러에서 처리할 필요가 없다 !
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        //GrantedAuthority : 사용자가 어떤 권한을 가지고 있는지 정의하는 인터페이스
        //현재 사용자의 권한을 가져온다.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();//첫 번째 권한을 꺼낸다.

        //usernaem, role 정보를 포함한 JWT(토큰)을 생성, 유효 시간 : 60초 * 60분 * 10 = 10시간
        String token = jwtUtil.createJWT(username, role, 60*60*10L);

        //JWT를 응답 헤더에 추가
        //HTTP 인증 방식은 RFC 7235 정의에 따라 다음과 같은 인증 헤더 형태를 가져야 한다 (Authorization : Bearer 인증토큰)
        response.addHeader("Authorization", "Bearer " + token);

    }

    //로그인 실패 시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }
}
