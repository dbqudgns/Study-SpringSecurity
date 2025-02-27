package com.Spring.OAuthSession.config;

import com.Spring.OAuthSession.oauth2.CustomClientRegistrationRepo;
import com.Spring.OAuthSession.oauth2.CustomOAuth2AuthorizedClientService;
import com.Spring.OAuthSession.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;

    /** SecurityFilterChain :
     * HTTP 요청을 보호하기 위해 동작하는 보안 필터들의 체인(묶음)
     * ex) 보안필터 : UsernamePasswordAuthenticationFilter, BasicAuthenticationFilter, OAuth2LoginAuthenticationFilter 등등
     * 아래 코드는 웹 어플리케이션의 모든 요청이 통과해야 하는 보안 필터들을 환경 설정한 것
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

        //oauth2Login : OAuth2 로그인을 활성화하고, OAuth2와 관련된 필터와 세팅을 자동으로 설정해줌
        //oauth2Client : OAuth2 로그인을 활성화하고, OAuth2와 관련된 필터와 세팅을 직접 커스텀해야 됨
        /** .userInfoEndpoint( ~ ) : OAuth2 제공자로부터 사용자 정보를 가져올 때 사용할 서비스를 지정
         * customOAuth2UserService라는 커스텀 서비스를 이용하여, 각 제공자로부터 받은 사용자 정보를 처리한다.
         */
        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login") //인증이 필요한 요청이 있을 때 사용자를 OAuth2의 기본 제공 로그인 페이지 대신 커스텀한 /login 앤드포인트로 리다이렉션 한다는 뜻
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository()) //커스터한 OAuth2 클라이언트 등록 정보 저장소를 적용
                        .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepo.clientRegistrationRepository()))
                        // ↑ : 인증 후 사용자의 토큰 정보 등을 DB에 저장하고 관리하도록 한다.
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)));

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated());

        return http.build();

    }

}
