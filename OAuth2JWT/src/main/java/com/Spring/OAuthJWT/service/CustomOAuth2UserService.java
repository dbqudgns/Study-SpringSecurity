package com.Spring.OAuthJWT.service;

import com.Spring.OAuthJWT.dto.*;
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

      }
      else if (registrationId.equals("google")) {

          //구글 로그인일 경우, 구글 전용 응답 객체(GoogleResponse) 생성
          oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

      }
      else {
          return null;
      }

      //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듦
      String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

      UserEntity existData = userRepository.findByUsername(username);

      //DB에 존재하지 않을 시 해당 유저 새로 생성
      if (existData == null) {

          UserEntity userEntity = new UserEntity();
          userEntity.setUsername(username);
          userEntity.setEmail(oAuth2Response.getEmail());
          userEntity.setName(oAuth2Response.getName());
          userEntity.setRole("ROLE_USER");

          userRepository.save(userEntity);

          UserDTO userDTO = new UserDTO();
          userDTO.setUsername(username);
          userDTO.setName(oAuth2Response.getName());
          userDTO.setRole("ROLE_USER");

          return new CustomOAuth2User(userDTO);
      }
      else { //DB에 존재하면 해당 유저 정보 업데이트

          existData.setEmail(oAuth2Response.getEmail());
          existData.setName(oAuth2Response.getName());

          UserDTO userDTO = new UserDTO();
          userDTO.setUsername(existData.getUsername());
          userDTO.setName(existData.getName());
          userDTO.setRole(existData.getRole());

          return new CustomOAuth2User(userDTO);
      }
    }
}
