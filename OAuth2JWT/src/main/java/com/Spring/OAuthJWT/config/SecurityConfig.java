package com.Spring.OAuthJWT.config;

import com.Spring.OAuthJWT.jwt.CustomSuccessHandler;
import com.Spring.OAuthJWT.jwt.JWTFilter;
import com.Spring.OAuthJWT.jwt.JWTUtil;
import com.Spring.OAuthJWT.oauth2.CustomClientRegistrationRepo;
import com.Spring.OAuthJWT.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    /** SecurityFilterChain :
     * HTTP 요청을 보호하기 위해 동작하는 보안 필터들의 체인(묶음)
     * ex) 보안필터 : UsernamePasswordAuthenticationFilter, BasicAuthenticationFilter, OAuth2LoginAuthenticationFilter 등등
     * 아래 코드는 웹 어플리케이션의 모든 요청이 통과해야 하는 필터 체인 을 환경 설정한 것
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //개발 환경에서는 CSRF 비활성화
        http
                .csrf((csrf) -> csrf.disable());

        //Form 로그인 방식은 진행 X
        http
                .formLogin((login) -> login.disable());

        //http basic 인증 방식 진행 X
        http
                .httpBasic((basic) -> basic.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //oauth2Login : OAuth2 로그인을 활성화하고, OAuth2와 관련된 필터와 세팅을 자동으로 설정해줌
        //oauth2Client : OAuth2 로그인을 활성화하고, OAuth2와 관련된 필터와 세팅을 직접 커스텀해야 됨
        /** .userInfoEndpoint( ~ ) : OAuth2 제공자로부터 사용자 정보를 가져올 때 사용할 서비스를 지정
         * customOAuth2UserService라는 커스텀 서비스를 이용하여, 각 제공자로부터 받은 사용자 정보를 처리한다.
         */
        http
                .oauth2Login((oauth2) -> oauth2
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository()) //커스텀한 OAuth2 클라이언트 등록 정보 저장소를 적용
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)); //로그인 성공 시 customSuccessHandler 클래스를 통해 JWT 발급 및 프론트단에게 전송

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll() // /oauth2/authorization/서비스명, /login/oauth2/code/서비스명은 자동으로 인증없이 접근 가능하도록 설정되어 있다.
                        .anyRequest().authenticated());

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    //프론트엔드에서 백엔드로 요청을 보낼 때 CORS 문제(다른 출처 리소스를 허용X)를 해결하기 위한 정책 설정
                    //그 중 Security Filter에서 예를 들어 JWT 반환이 안될 때
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //3000에서 오는 요청만 허용
                        configuration.setAllowedMethods(Collections.singletonList("*")); //허용 할 HTTP 메서드 설정 (여기서는 모든 HTTP 메서드 허용)
                        configuration.setAllowCredentials(true); //true : 요청에 Authorization 헤더나 Cookie를 포함할 수 있음
                        configuration.setAllowedHeaders(Collections.singletonList("*")); //클라이언트가 요청할 때 허용할 헤더 설정 (여기서는 모든 헤더 허용)
                        configuration.setMaxAge(3600L); //사전 요청 : 브라우저가 서버에게 api 요청을 보내도 되는지 먼저 확인하는 과정
                        //setMaxAge(3600L) : 1시간 동안 같은 요청의 사전 요청을 생략할 수 있다.

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie")); //클라이언트가 응답 헤더 중 "Set-Cookie" 헤더를 읽을 수 있도록 허용
                        //configuration.setExposedHeaders(Collections.singletonList("Authorization")); //클라이언트가 응답 헤더 중 "Authorization" 헤더를 접근할 수 있도록 허용

                        return configuration;
                    }
                }));

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }

}
