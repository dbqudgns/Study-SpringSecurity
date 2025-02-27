package com.Spring.OAuthSession.service;

import com.Spring.OAuthSession.dto.CustomOAuth2User;
import com.Spring.OAuthSession.dto.GoogleResponse;
import com.Spring.OAuthSession.dto.NaverResponse;
import com.Spring.OAuthSession.dto.OAuth2Response;
import com.Spring.OAuthSession.entity.UserEntity;
import com.Spring.OAuthSession.repository.UserRepository;
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
          System.out.println("ㅇㅇㅇ");
          return null;
      }

      String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
      UserEntity existData = userRepository.findByUsername(username);

      String role = "ROLE_USER";
      if (existData == null) { //사용자가 처음 로그인한 경우 DB에 저장

          UserEntity userEntity = new UserEntity();
          userEntity.setUsername(username);
          userEntity.setEmail(oAuth2Response.getEmail());
          userEntity.setRole(role);

          userRepository.save(userEntity);
      }
      else { //사용자의 기존 로그인 정보를 DB에 업데이트

          existData.setUsername(username);
          existData.setEmail(oAuth2Response.getEmail());

          role = existData.getRole();

      }

      return new CustomOAuth2User(oAuth2Response, role);

    }
}
