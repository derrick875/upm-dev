package com.nets.nps.client.security.configuration;

import com.nets.upos.commons.logger.ApsLogger;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Configuration
public class SSLConfiguration {

    private static final ApsLogger logger = new ApsLogger(SSLConfiguration.class);

    @Autowired
    KeyStore keyStore;
    @Autowired
    KeyStore trustStore;

    @Value("${client.ssl.key-store.key.password}")
    String keyPass;

    @Bean
    public SslContext get2WaySSLContext() {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyPass.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            return SslContextBuilder.forClient()
                    .keyManager(keyManagerFactory)
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            logger.error("Error creating 2-Way TLS WebClient. Check key-store and trust-store.", e);
        }

        return null;
    }

}
