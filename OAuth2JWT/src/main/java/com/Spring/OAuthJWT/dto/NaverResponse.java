package com.Spring.OAuthJWT.dto;

import java.util.Map;

/**
 * 네이버 데이터 : JSON
 * {resultcode=00, message=success, response={id=5JKiwMsA125OMi4U email=dbqudgns24@naver.com, name=유병훈}}
 */

//Naver 응답 구현체
public record NaverResponse(Map<String, Object> attribute) implements OAuth2Response {

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String,Object>)attribute.get("response");
    }

    @Override
    public String getProvider() {
       return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
