package com.nets.nps.client.security.configuration;

import com.nets.upos.commons.logger.ApsLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class KeyStoreConfiguration {

    private static final ApsLogger logger = new ApsLogger(KeyStoreConfiguration.class);

    @Value("${client.ssl.trust-store}")
    String trustStorePath;
    @Value("${client.ssl.trust-store.password}")
    String trustStorePass;
    @Value("${client.ssl.key-store}")
    String keyStorePath;
    @Value("${client.ssl.key-store.password}")
    String keyStorePass;

    @Bean
    public KeyStore keyStore() throws Exception {
        try {
            logger.info("Keystore: " + keyStorePath);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(ResourceUtils.getFile(keyStorePath)), keyStorePass.toCharArray());

            return keyStore;
        } catch (Exception e) {
            logger.error("Failed to load keystore.", e);
            throw e;
        }
    }

    @Bean
    public KeyStore trustStore() throws Exception {
        try {
            logger.info("Truststore: " + trustStorePath);
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(new FileInputStream(ResourceUtils.getFile(trustStorePath)), trustStorePass.toCharArray());

            return trustStore;
        } catch (Exception e) {
            logger.error("Failed to load keystore.", e);
            throw e;
        }
    }
}
