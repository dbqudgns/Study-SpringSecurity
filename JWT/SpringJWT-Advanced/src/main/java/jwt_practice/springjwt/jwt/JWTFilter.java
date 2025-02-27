package jwt_practice.springjwt.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt_practice.springjwt.entity.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

//로그인한 사용자의 JWT를 이용한 인증을 처리하는 필터
//요청이 들어올 때마다 실행되며, 사용자 인증을 수행하는 역할
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더에 담긴 Access 토큰을 꺼낸다.
        String token = request.getHeader("Authorization");

        //토큰이 없다면 다음 필터로 넘긴다.
        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("token null or token not start with Bearer ");
            filterChain.doFilter(request, response);

            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String accessToken = token.split(" ")[1];

        //토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter(); //response.getWriter()는 응답 body에 데이터를 작성할 수 있도록 PrintWriter 객체를 반환한다.
            writer.print("access token expired"); //응답 body에 출력된다.

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 상태 코드 설정
            return; //만료시 다음 필터로 절대 넘기면 안됨 !! (중요)
        }

        //토큰의 category가 access인지 확인 (발급 시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response staus code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; //access가 아닐 시 다음 필터로 절대 넘기면 안됨 !! (중요)
        }


        //JWT 토큰을 파싱하여 username과 role을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        //UserEntity를 생성하여 값을 설정
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체 담기, UserEntity 정보를 바탕으로 사용자 객체를 생성한다.
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성 : 인증된 사용자 객체, 비밀번호, 사용자 권한 목록
        //비밀번호 null인 이유 : JWT는 이미 서버에서 발급한 토큰이므로 별도로 비밀번호 검증을 하지 않는다.
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails,null, customUserDetails.getAuthorities());

        //세션에 사용자 등록 : 이후 컨트롤러에서 @AuthenticationPrincipal 등을 사용해 현재 로그인한 사용자를 가져올 수 있다.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //필터 처리가 끝난 후 요청을 다음 필터로 넘긴다
        filterChain.doFilter(request, response);


    }
}
