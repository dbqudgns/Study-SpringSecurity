package com.Spring.OAuthSession.oauth2;

import com.Spring.OAuthSession.dto.CustomOAuth2User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.jdbc.core.SqlParameterValue;

import org.springframework.util.Assert;
import java.util.List;

@RequiredArgsConstructor
public class CustomJdbcOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private static final String INSERT_CLIENT_SQL =
            "INSERT INTO oauth2_authorized_client (client_registration_id, principal_name, access_token_type, access_token_value, access_token_issued_at, access_token_expires_at, access_token_scopes, refresh_token_value, refresh_token_issued_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CLIENT_SQL =
            "UPDATE oauth2_authorized_client SET access_token_type = ?, access_token_value = ?, access_token_issued_at = ?, access_token_expires_at = ?, access_token_scopes = ?, refresh_token_value = ?, refresh_token_issued_at = ? WHERE client_registration_id = ? AND principal_name = ?";

    private static final String DELETE_CLIENT_SQL =
            "DELETE FROM oauth2_authorized_client WHERE client_registration_id = ? AND principal_name = ?";

    private static final String SELECT_CLIENT_SQL =
            "SELECT * FROM oauth2_authorized_client WHERE client_registration_id = ? AND principal_name = ?";

    private final JdbcOperations jdbcOperations;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {

        Assert.hasText(clientRegistrationId, "clientRegistrationId에 문자열 값이 없습니다.");
        Assert.hasText(principalName, "principalName에 문자열 값이 없습니다.");

        List<OAuth2AuthorizedClient> results = jdbcOperations.query(SELECT_CLIENT_SQL,
                new Object[]{clientRegistrationId, principalName}, // SQL에 들어갈 파라미터 배열 선언
                new JdbcOAuth2AuthorizedClientService.OAuth2AuthorizedClientRowMapper(clientRegistrationRepository));
                // ↑ : 쿼리 결과로 나온 ResultSet을 OAuth2AuthorizedClient로 변환해주는 RowMapper 객체를 생성

        return results.isEmpty() ? null : (T) results.get(0);

    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {

        Assert.notNull(authorizedClient, "authorizedClient은 Null이면 안됩니다.");
        Assert.notNull(principal, "principal은 Null이면 안됩니다.");

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal.getPrincipal();
        String username = customOAuth2User.getUsername();

        boolean existsClient = null != loadAuthorizedClient(authorizedClient.getClientRegistration().getRegistrationId(), username);

        if (existsClient) {
            updateAuthorizedClient(authorizedClient, username);
        } else {
            try {
                insertAuthorizedClient(authorizedClient, username);
            } catch (DuplicateKeyException e) {
                updateAuthorizedClient(authorizedClient, username);
            }
        }

    }


    protected void insertAuthorizedClient(OAuth2AuthorizedClient authorizedClient, String username) {

        List<SqlParameterValue> parameters = SqlParameters.getInsertParameters(authorizedClient, username);
        jdbcOperations.update(INSERT_CLIENT_SQL, parameters.toArray());

    }

    protected void updateAuthorizedClient(OAuth2AuthorizedClient authorizedClient, String username) {

        List<SqlParameterValue> parameterValues = SqlParameters.getUpdateParameters(authorizedClient, username);
        jdbcOperations.update(UPDATE_CLIENT_SQL, parameterValues.toArray());

    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String username) {

        Assert.hasText(clientRegistrationId, "clientRegistrationId에 문자열 값이 없습니다.");
        Assert.hasText(username, "username에 문자열 값이 없습니다.");

        List<SqlParameterValue> parameters = SqlParameters.getDeleteParameters(clientRegistrationId, username);
        jdbcOperations.update(DELETE_CLIENT_SQL, parameters.toArray());

    }
}
