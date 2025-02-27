package com.Spring.OAuthSession.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//사용자 정보를 담는 객체(DTO)
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    //GrantedAuthority : 사용자가 어떤 권한을 가지고 있는지 정의하는 인터페이스
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {

            //현재 사용자(UserEntity)의 권한을 반환하는 메서드
            @Override
            public String getAuthority() {
                return role;
            }

        });

        return collection;
    }

    //제공자에 준 사용자의 닉네임
    @Override
    public String getName() {
        return oAuth2Response.getName();
    }

    //제공자에서 준 사용자의 임의 아이디 값 반환
    public String getUsername() {
        return oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
    }
}
