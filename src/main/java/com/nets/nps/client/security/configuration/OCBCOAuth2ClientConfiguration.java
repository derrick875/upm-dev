package com.nets.nps.client.security.configuration;

import com.nets.upos.commons.logger.ApsLogger;
import io.netty.handler.ssl.SslContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;

@Configuration
public class OCBCOAuth2ClientConfiguration {

    private static final ApsLogger logger = new ApsLogger(OCBCOAuth2ClientConfiguration.class);

    @Value("${client.ssl.key-store.key.password}")
    private String keyPass;

    @Value("${spring.security.oauth2.client.request.timeout:3000}")
    private int requestTimeout;

    @Value("${spring.security.oauth2.client.response.timeout:3000}")
    private int responseTimeout;

    @Bean
    RestTemplate restTemplate(KeyStore keyStore, KeyStore trustStore, SslContext twoWaySsLContext) throws Exception {
        try {
            logger.info("requestTimeout: " + requestTimeout);

            SSLContext sslcontext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, keyPass.toCharArray())
                    .loadTrustMaterial(trustStore, null).build();

            // Uncomment if there is 2-way ssl issue for debugging
//            try {
//                logger.info("trustStore: " + trustStore);
//                logger.info("keyStore: " + keyStore);
//                logger.info("Contain OCBC Root: " + trustStore.containsAlias("digicertrootca"));
//                logger.info("Contain Nets Root: " + trustStore.containsAlias("entrustnetca"));
//                logger.info("Contains nets public: " + keyStore.containsAlias("uat-paynow-cps.nets.com.sg"));
//                logger.info("Contains intermediate ocbc: " + keyStore.containsAlias("digicertintermediateca"));
//                logger.info("Contains intermediate nets: " + keyStore.containsAlias("entrustnetintermediateca"));
//            } catch (Throwable t) {
//                logger.error("Error to validate keystore.", t);
//            }

            CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslcontext).build();

            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            requestFactory.setConnectionRequestTimeout(requestTimeout);
            requestFactory.setReadTimeout(responseTimeout);

            OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                    new OAuth2AccessTokenResponseHttpMessageConverter();
            tokenResponseHttpMessageConverter.setTokenResponseConverter(new OCBCOAuth2ResponseConverter());
            RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
            restTemplate.setRequestFactory(requestFactory);
            restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

            return restTemplate;
        } catch (Exception e) {
            logger.error("Error create RestTemplate ", e);
            return null;
        }
    }

    @Bean
    public Converter<OAuth2ClientCredentialsGrantRequest, RequestEntity<?>> ocbcClientCredentialRequestConverter() {
        return new OCBCRequestEntityConverter();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient(RestTemplate restOperation) {
        DefaultClientCredentialsTokenResponseClient client =
                new DefaultClientCredentialsTokenResponseClient();
        client.setRequestEntityConverter(ocbcClientCredentialRequestConverter());
        client.setRestOperations(restOperation);

        return client;
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService,
            // OAuth2AuthorizedClientRepository authorizedClientRepository,
            RestTemplate restOperation) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        // So far OCBC only support client credentials and no token refresh
                        .clientCredentials(builder -> builder.accessTokenResponseClient(accessTokenResponseClient(restOperation)).build())
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
