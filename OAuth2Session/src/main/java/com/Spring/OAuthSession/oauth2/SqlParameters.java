package com.Spring.OAuthSession.oauth2;

import org.springframework.util.CollectionUtils;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SqlParameters {

    public static List<SqlParameterValue> getInsertParameters(OAuth2AuthorizedClient authorizedClient, String username) {

        List<SqlParameterValue> parameters = new ArrayList<>();
        ClientRegistration clientRegistration = authorizedClient.getClientRegistration();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        parameters.add(new SqlParameterValue(12, clientRegistration.getRegistrationId()));
        parameters.add(new SqlParameterValue(12, username));
        parameters.add(new SqlParameterValue(12, accessToken.getTokenType().getValue())); //Bearer
        parameters.add(new SqlParameterValue(2004, accessToken.getTokenValue().getBytes(StandardCharsets.UTF_8)));
        parameters.add(new SqlParameterValue(93, Timestamp.from(accessToken.getIssuedAt())));
        parameters.add(new SqlParameterValue(93, Timestamp.from(accessToken.getExpiresAt())));

        //Scope : Access Token이 접근할 수 있는 자원 범위
        String accessTokenScopes = CollectionUtils.isEmpty(accessToken.getScopes()) ? null
                : StringUtils.collectionToDelimitedString(accessToken.getScopes(), ",");
        //StringUtils.collectionToDelimitedString : Scope의 Collection을 쉼표(,)로 구분된 문자열로 변환
        parameters.add(new SqlParameterValue(12, accessTokenScopes));

        byte[] refreshTokenValue = refreshToken != null ? refreshToken.getTokenValue().getBytes(StandardCharsets.UTF_8) : null;
        parameters.add(new SqlParameterValue(2004, refreshTokenValue));

        Timestamp refreshTokenIssuedAt = (refreshToken != null && refreshToken.getIssuedAt() != null) ?
                Timestamp.from(refreshToken.getIssuedAt()) : null;
        parameters.add(new SqlParameterValue(93, refreshTokenIssuedAt));

        return parameters;

    }

    public static List<SqlParameterValue> getUpdateParameters(OAuth2AuthorizedClient authorizedClient, String username) {

        List<SqlParameterValue> parameters = new ArrayList<>();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        parameters.add(new SqlParameterValue(12, accessToken.getTokenType().getValue())); //Bearer
        parameters.add(new SqlParameterValue(2004, accessToken.getTokenValue().getBytes(StandardCharsets.UTF_8)));
        parameters.add(new SqlParameterValue(93, Timestamp.from(accessToken.getIssuedAt())));
        parameters.add(new SqlParameterValue(93, Timestamp.from(accessToken.getExpiresAt())));

        //Scope : Access Token이 접근할 수 있는 자원 범위
        String accessTokenScopes = CollectionUtils.isEmpty(accessToken.getScopes()) ? null
                : StringUtils.collectionToDelimitedString(accessToken.getScopes(), ",");
        //StringUtils.collectionToDelimitedString : Scope의 Collection을 쉼표(,)로 구분된 문자열로 변환
        parameters.add(new SqlParameterValue(12, accessTokenScopes));

        byte[] refreshTokenValue = refreshToken != null ? refreshToken.getTokenValue().getBytes(StandardCharsets.UTF_8) : null;
        parameters.add(new SqlParameterValue(2004, refreshTokenValue));

        Timestamp refreshTokenIssuedAt = (refreshToken != null && refreshToken.getIssuedAt() != null) ?
                Timestamp.from(refreshToken.getIssuedAt()) : null;
        parameters.add(new SqlParameterValue(93, refreshTokenIssuedAt));

        parameters.add(new SqlParameterValue(12, authorizedClient.getClientRegistration().getRegistrationId())); //WHERE 절
        parameters.add(new SqlParameterValue(12, username));// WHERE 절

        return parameters;

    }

    public static List<SqlParameterValue> getDeleteParameters(String clientRegistrationId, String principalName) {

        List<SqlParameterValue> parameters = new ArrayList<>();

        parameters.add(new SqlParameterValue(12, clientRegistrationId));
        parameters.add(new SqlParameterValue(12, principalName));

        return parameters;
    }
}
