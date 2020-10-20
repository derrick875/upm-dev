package com.nets.nps.client.security.configuration;

import com.nets.nps.paynow.client.bank.ocbc.Signer;
import com.nets.upos.commons.logger.ApsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Collectors;

import static com.nets.nps.paynow.client.bank.ocbc.OcbcPaynowAdapter.HEADER_SIGNATURE;
import static com.nets.nps.paynow.client.bank.ocbc.OcbcPaynowAdapter.HEADER_TIMESTAMP;

public class OCBCRequestEntityConverter implements Converter<OAuth2ClientCredentialsGrantRequest, RequestEntity<?>> {

    private final static ApsLogger logger = new ApsLogger(OCBCRequestEntityConverter.class);

    @Autowired
    private Signer signer;

    @Value("${spring.security.oauth2.client.registration.ocbc.application}")
    private String applicationName;

    private OAuth2ClientCredentialsGrantRequestEntityConverter defaultConverter;

    public OCBCRequestEntityConverter() {
        defaultConverter = new OAuth2ClientCredentialsGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(OAuth2ClientCredentialsGrantRequest request) {
        RequestEntity<?> entity = defaultConverter.convert(request);

        MultiValueMap<String, String> params = (MultiValueMap<String, String>) entity.getBody();
        params.add("application", applicationName);

        HttpHeaders headers = entity.getHeaders();
        HttpHeaders ocbcHeaders = new HttpHeaders();
        ocbcHeaders.addAll(headers);
        ocbcHeaders.add("Authorization", "");
        ocbcHeaders.add("Client-Id", params.getFirst("client_id"));

        try {
            long timestamp = Instant.now().getEpochSecond();
            String payload = params.keySet().stream()
                    .map(key -> key + "=" + encodeValue(params.getFirst(key)))
                    .collect(Collectors.joining("&"));

            String input = "Authorization:;" + HEADER_TIMESTAMP + ":" + timestamp + ";" +
                    "Resource:" + entity.getUrl().getPath() + ";" + payload;
            String signature = signer.sign(input).toString();

            ocbcHeaders.add(HEADER_TIMESTAMP, String.valueOf(timestamp));
            ocbcHeaders.add(HEADER_SIGNATURE, signature);
        } catch (Exception e) {
            logger.error("Failed to sign the header", e);
            throw new RuntimeException(e);
        }

        return new RequestEntity<>(params, ocbcHeaders, entity.getMethod(), entity.getUrl());
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            logger.error("Failed to encode the value: " + value);
            throw new RuntimeException(e);
        }
    }
}
