//추후 작성 예정

/*package com.Spring.OAuthSession.oauth2;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class CustomJdbcOAuth2AuthorizedClientService extends JdbcOAuth2AuthorizedClientService {

    public CustomJdbcOAuth2AuthorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
        super(jdbcOperations, clientRegistrationRepository);
    }

    public CustomJdbcOAuth2AuthorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository, LobHandler lobHandler) {
        super(jdbcOperations, clientRegistrationRepository, lobHandler);
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return super.loadAuthorizedClient(clientRegistrationId, principalName);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        super.saveAuthorizedClient(authorizedClient, principal);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        super.removeAuthorizedClient(clientRegistrationId, principalName);
    }
}*/
