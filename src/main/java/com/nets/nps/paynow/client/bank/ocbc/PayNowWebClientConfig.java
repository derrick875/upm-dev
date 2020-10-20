package com.nets.nps.paynow.client.bank.ocbc;

import com.nets.upos.commons.logger.ApsLogger;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Instant;
import java.util.Objects;

@Configuration
public class PayNowWebClientConfig {

    private static final ApsLogger logger = new ApsLogger(PayNowWebClientConfig.class);

    public static final String REGISTRATION_ID = "ocbc";

    @Value("${bank.ocbc.api.baseurl}")
    String baseUrl;

    @Value("${spring.security.oauth2.client.registration.ocbc.client-id}")
    String clientId;

    @Value("${bank.ocbc.api.timeout}")
    int timeout;

    @Autowired
    Signer signer;

    @Bean(name = "ocbc-paynow-webclient")
    public WebClient create2WayTLSWebClient(SslContext twoWaySsLContext,
                                             OAuth2AuthorizedClientManager authorizedClientManager) {
        HttpClient httpClient = HttpClient.create()
                .disableRetry(true)
                .secure(sslSpec -> sslSpec.sslContext(twoWaySsLContext))
                .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout));

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId(REGISTRATION_ID);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                .apply(oauth2Client.oauth2Configuration())
                .filter(signAPIRequest())
                .filter(logRequest())
                .build();
    }

    public ExchangeFilterFunction signAPIRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            try {
                String authorization = clientRequest.headers().getFirst(HttpHeaders.AUTHORIZATION);
                if (!Objects.isNull(authorization)) {
                    String payload = clientRequest.headers().getFirst(OcbcPaynowAdapter.HEADER_TEMP_PAYLOAD);
                    String timestamp = String.valueOf(Instant.now().getEpochSecond());

                    // Refer OCBC API Gateway Specification 5.1.1
                    String input = "Authorization:" + authorization + ";" +
                            OcbcPaynowAdapter.HEADER_TIMESTAMP + ":" + timestamp + ";" +
                            "Resource:" + clientRequest.url().getPath() + ";" + payload;

                    ClientRequest filteredClientRequest = ClientRequest.from(clientRequest)
                            .headers(headers -> {
                                try {
                                    headers.add(OcbcPaynowAdapter.HEADER_TIMESTAMP, timestamp);
                                    headers.add(OcbcPaynowAdapter.HEADER_SIGNATURE, signer.sign(input).toString());
                                    headers.add(OcbcPaynowAdapter.HEADER_CLIENT_ID, clientId);
                                    headers.remove(OcbcPaynowAdapter.HEADER_TEMP_PAYLOAD);
                                }  catch (Exception e) {
                                    logger.error("Error when set the customized header", e);
                                    throw new RuntimeException(e);
                                }
                            })
                            .build();
                    return Mono.just(filteredClientRequest);
                } else {
                    return Mono.just(clientRequest);
                }
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("Request: " + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> logger.info(name + "=" + value)));
            return Mono.just(clientRequest);
        });
    }

}
