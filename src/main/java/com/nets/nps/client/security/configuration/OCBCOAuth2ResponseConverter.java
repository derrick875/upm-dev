package com.nets.nps.client.security.configuration;

import com.nets.upos.commons.logger.ApsLogger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OCBCOAuth2ResponseConverter implements Converter<Map<String, String>, OAuth2AccessTokenResponse> {

    private final static ApsLogger logger = new ApsLogger(OCBCOAuth2ResponseConverter.class);

    public final static String SCOPES_SEPARATOR = " ";

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParams) {

        String accessToken = tokenResponseParams.get(OAuth2ParameterNames.ACCESS_TOKEN);
        long expiresIn = Long.valueOf(tokenResponseParams.get(OAuth2ParameterNames.EXPIRES_IN));
        String scope = tokenResponseParams.get("scope");
        logger.info("accessToke " + accessToken + ", expiresIn: " + expiresIn + ", scope: " + scope);
        Set<String> scopes = Arrays.stream(scope.split(SCOPES_SEPARATOR)).collect(Collectors.toSet());

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(expiresIn)
                .scopes(scopes)
                .build();
    }
}