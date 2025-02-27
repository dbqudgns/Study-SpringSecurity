package com.Spring.OAuthJWT.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//프론트엔드에서 백엔드로 요청을 보낼 때 CORS 문제(다른 출처 리소스를 허용X)를 해결하기 위한 정책 설정
//그 중 Security Filter를 지나쳐 Controller 단까지 오는 요청일 경우의 CORS 설정
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**") //모든 경로(/**)에 대해 CORS 설정을 적용
                .exposedHeaders("Set-Cookie") //클라이언트가 응답 헤더 중 "Set-Cookie" 헤더를 읽을 수 있도록 허용
                .allowedOrigins("http://localhost:3000"); //3000에서 오는 요청만 허용

    }
}
