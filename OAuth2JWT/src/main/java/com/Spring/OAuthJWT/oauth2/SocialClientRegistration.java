package com.Spring.OAuthJWT.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

//소셜 로그인 제공 서비스에 대한 정보 기입을 커스텀 클래스를 통해 진행
@Component
public class SocialClientRegistration {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientIdNaver;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecretNaver;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientIdGoogle;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecretGoogle;

    //ClientRegistration : 제공자별 OAuth2 클라이언트의 등록 정보를 가지는 클래스
    //Naver
    public ClientRegistration naverClientRegistration() {

        return ClientRegistration.withRegistrationId("naver") //클라이언트 식별자를 "naver"로 지정
                .clientId(clientIdNaver) //네이버에서 발급받은 클라이언트 ID
                .clientSecret(clientSecretNaver) //네이버에서 발급받은 클라이언트 Secret
                .redirectUri("http://localhost:8080/login/oauth2/code/naver") //2. 인증 완료(로그인 성공)된 후 네이버가 인증 성공(인가 Code)을 알리는 리다이렉트 URL
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) //발급받은 인가 Code를 통해 토큰을 요청하는 "인가 Code" 방식을 알림
                .scope("name", "email") //웹 애플리케이션이 네이버로부터 요청할 사용자 정보의 범위를 지정
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize") //1. 사용자를 네이버 로그인 페이지로 안내할 때 지정한 URL
                //ex : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=D7to1oza0WhlceGxVWnO&scope=name%20email&state=FAF5peUu2sbDl9Ck0sn2xwe_TjjN-Cmv-SWJB0N08BQ%3D&redirect_uri=http://localhost:8080/login/oauth2/code/naver
                .tokenUri("https://nid.naver.com/oauth2.0/token") //3. 인가 Code를 Access 토큰으로 교환하기 위한 네이버의 토큰 발급 URL
                .userInfoUri("https://openapi.naver.com/v1/nid/me") //4. Access 토큰을 사용해 네이버로부터 사용자 정보를 요청할 때 호출하는 API URL
                .userNameAttributeName("response")
                .build();
    }

    //Google
    public ClientRegistration googleClientRegistration() {

        return ClientRegistration.withRegistrationId("google")
                .clientId(clientIdGoogle)
                .clientSecret(clientSecretGoogle)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs") //토큰 서명 검증을 위한 공개키를 제공 : 애플리케이션은 이 URL을 통해 받은 공개키로 토큰의 서명을 검증하여 구글에서 만들어졌음을 확인할 수 있음
                .issuerUri("https://accounts.google.com") //토큰 내부에 기록된 발급자(issuer) 정보가 실제 구글과 일치하는지 검증하는데 사용
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB) //사용자 정보를 식별할 때 사용할 속성을 지정, 여기서는 "sub"을 사용자 식별자로 사용
                .build();
    }
}

