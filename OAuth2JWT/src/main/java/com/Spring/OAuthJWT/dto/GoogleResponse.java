package com.Spring.OAuthJWT.dto;

import java.util.Map;

/**
 * 구글 데이터 : JSON
 * {sub=1088401, name=유병훈, given_name=병훈, family_name=유, picture="구글 프로필 이미지 URI", email=dbqudgns4632@gmail.com, email_verified=true}
 */

//Google 응답 구현체
public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
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
