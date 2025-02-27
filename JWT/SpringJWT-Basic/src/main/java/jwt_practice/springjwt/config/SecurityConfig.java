package jwt_practice.springjwt.config;

import jakarta.servlet.http.HttpServletRequest;
import jwt_practice.springjwt.jwt.JWTFilter;
import jwt_practice.springjwt.jwt.JWTUtil;
import jwt_practice.springjwt.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration //스프링 애플리케이션 환경설정
@EnableWebSecurity //Spring security의 웹 보안을 활성화하고 사용자 정의 구성을 가능하게 함
//Spring Security의 인증/인가 및 설정을 담당하는 클래스
public class SecurityConfig {

    //2.
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    //3. AuthenticationManager 생성을 위한 AuthenticationConfiguration 객체 생성자 주입
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    //4. AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
        // BCryptPasswordEncoder :
        // 사용자가 입력한 원래 비밀번호를 안전하게 암호화(해싱)하는데 사용된다.
        // 해싱된 암호는 복호화가 불가능하지만, 입력값과 해시값의 일치 여부를 검증할 수 있다.
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //프론트엔드에서 백엔드로 요청을 보낼 때 CORS 문제(다른 출처 리소스를 허용X)를 해결하기 위한 정책 설정
        //그 중 Security Filter에서 예를 들어 JWT 반환이 안될 때
        http.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() { //corsCustomizer : CORS 정책 설정을 담당하는 CorsConfigurer<HttpSecurity> 객체

            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //3030에서 오는 요청만 허용
                configuration.setAllowedMethods(Collections.singletonList("*")); //허용 할 HTTP 메서드 설정 (여기서는 모든 HTTP 메서드 허용)
                configuration.setAllowCredentials(true); //true :요청에 Authorization 헤더나 Cookie를 포함할 수 있음
                configuration.setAllowedHeaders(Collections.singletonList("*")); //클라이언트가 요청할 때 허용할 헤더 설정 (여기서는 모든 헤더 허용)
                configuration.setMaxAge(3600L); //사전 요청 : 브라우저가 서버에게 api 요청을 보내도 되는지 먼저 확인하는 과정
                //setMaxAge(3600L) : 1시간 동안 같은 요청의 사전 요청을 생략할 수 있다.
                configuration.setExposedHeaders(Collections.singletonList("Authorization")); //클라이언트에가 응답에서 볼 수 있는 헤더 설정

                return configuration;
            }
        })));

        /** CSRF 공격 :
         * 사용자가 자신의 의지와는 다르게 공격자가 의도한 수정, 삭제, 등록 등의 행위를
         * 사용자가 사용하는 웹 사이트에 요청하게 만드는 공격
         **/
        //JWT 방식은 세션을 stateless 방식으로 관리하기 때문에 CSRF 공격을 방어하지 않아도된다.
        http.csrf((auth) -> auth.disable()); //auth : CSRF 보호를 설정할 수 있는 CsrfConfigurer<HttpSecurity> 객체

        //Form 로그인 방식 disable : Spring Security의 기본 로그인 페이지를 비활성화 시킨다.
        //Formlogin 방식은 기본적인 세션 기능을 가지고 있지만, JWT는 세션이 필요없으니 비활성화 시키는 것
        //UsernamePasswordAuthentication 필터를 비활성화 시킨 것
        http.formLogin((auth) -> auth.disable()); //auth : 폼 로그인 인증 기반을 담당하는 FormLoginConfigurer<HttpSecurity> 객체

        //http basic 인증 방식 disable : http basic은 ID와 비밀번호를 http 헤더에 포함시켜 서버에 보내는 인증 방식, JWT 토큰을 사용하므로 필요없다.
        http.httpBasic((auth) -> auth.disable()); //auth : HTTP Basic 인증을 설정하는 HttpBasicConfigurer<HttpSecurity> 객체

        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth //auth : 요청 별 인증 및 권한을 설정하는 AuthorizeHttpRequestsConfigurer<HttpSecurity> 객체
                .requestMatchers("/login", "/", "/join").permitAll() // /login, /, /join 경로 누구나 접근 가능
                .requestMatchers("/admin").hasRole("ADMIN") // /admin 경로는 ADMIN 권한을 가진 사용자만 접근 가능
                .anyRequest().authenticated()); //그 외의 모든 경로는 인증이 필요하다.

        //JWTFilter 등록
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        //1. UsernamePasswordAuthenticationFilter 대신 LoginFilter로 대체
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정 (필수 설정!!) : 서버가 세션을 생성하거나 유지하지 않음을 의미한다.
        //STATELESS : 서버가 클라이언트의 상태를 유지하지 않음
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();

    }
}
