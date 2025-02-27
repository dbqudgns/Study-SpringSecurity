package com.Spring.OAuthSession.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
//JDBC를 활용하여 OAuth2 인증 정보를 DB에 CRUD 하는 기능을 제공
public class CustomOAuth2AuthorizedClientService {

    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcTemplate jdbcTemplate, ClientRegistrationRepository clientRegistrationRepository) {

        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository); //jdbcTemplate을 통해 SQL문으로 DB의 토큰 정보를 CRUD 한다.
    }

}
