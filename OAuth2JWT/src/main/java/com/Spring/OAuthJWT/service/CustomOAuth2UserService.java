package com.Spring.OAuthJWT.service;

import com.Spring.OAuthJWT.dto.*;
import com.Spring.OAuthJWT.entity.Role;
import com.Spring.OAuthJWT.entity.UserEntity;
import com.Spring.OAuthJWT.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 제공자(Google, Naver, ..)의 리소스 서버에서 유저 정보를 받아와 처리하는 서비스
 * DefaultOAuth2UserService OAuth2UserService의 구현체
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //OAuth2 제공자로부터 사용자 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User = {}", oAuth2User.getAttributes());

        //어떤 OAuth2 제공자인지 식별하는 ID를 가져온다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //사용자 정보를 처리할 때 사용할 객체 선언
        OAuth2Response oAuth2Response = null;

        //제공자에 따라 다른 방식으로 사용자 정보를 처리
        if (registrationId.equals("naver")) {
            //네이버 로그인일 경우, 네이버 전용 응답 객체(NaverResponse) 생성
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            //구글 로그인일 경우, 구글 전용 응답 객체(GoogleResponse) 생성
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값(식별자)을 만듦
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        //DB 저장 또는 업데이트
        SaveOrUpdateUser(oAuth2Response, username);

        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .name(oAuth2Response.getName())
                .email(oAuth2Response.getEmail())
                .role(Role.USER)
                .build();

        return new CustomOAuth2User(userDTO);
    }

    private void SaveOrUpdateUser(OAuth2Response oAuth2Response, String username) {

        UserEntity existUser = userRepository.findByUsername(username);

        //DB에 사용자 정보가 없을 경우
        if (existUser == null) {

           UserEntity userEntity = UserEntity.builder()
                   .username(username)
                   .name(oAuth2Response.getName())
                   .email(oAuth2Response.getEmail())
                   .role(Role.USER)
                   .build();

           userRepository.save(userEntity);

        } else { //DB에 사용자 정보가 존재할 경우 => 업데이트
            existUser.updateUserEntity(oAuth2Response.getName(), oAuth2Response.getEmail(), Role.USER);
        }
    }
}
