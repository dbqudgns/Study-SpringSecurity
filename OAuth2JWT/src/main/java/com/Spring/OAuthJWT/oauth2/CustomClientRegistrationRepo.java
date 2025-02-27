package com.Spring.OAuthJWT.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
@RequiredArgsConstructor
public class CustomClientRegistrationRepo {

    private final SocialClientRegistration socialClientRegistration;

    //ClientRegistrationRepository : ClientRegistration의 저장소로 제공자 별 ClientRegistration들을 가진다.
    public ClientRegistrationRepository clientRegistrationRepository() {

        //2가지 서비스 밖에 없으므로 메모리로 저장해서 관리 !
        return new InMemoryClientRegistrationRepository(socialClientRegistration.naverClientRegistration(),
                                                        socialClientRegistration.googleClientRegistration());

    }


}
