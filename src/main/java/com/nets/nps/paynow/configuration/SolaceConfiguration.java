package com.nets.nps.paynow.configuration;

import com.solacesystems.jms.SupportedProperty;
import com.solacesystems.jndi.SolJNDIInitialContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiTemplate;

import javax.naming.InitialContext;
import java.util.Properties;

@Configuration
@Profile("!local")
public class SolaceConfiguration {

    @Value("${solace.url}")
    private String url;

    @Value("${solace.username}")
    private String username;

    @Value("${solace.vpn}")
    private String vpn;

    @Value("${solace.jms.ssl.validate_certificate}")
    private String certificate;

    @Value("${solace.jms.ssl.keystore}")
    private String keystore;

    @Value("${solace.jms.ssl.keystore.password}")
    private String keystorePassword;

    @Value("${solace.jms.ssl.authentication.scheme}")
    private String authenticationScheme;

    @Value("${solace.jms.ssl.privatekey.alias}")
    private String privateKeyAlias;

    @Value("${solace.jms.ssl.privatekey.password}")
    private String privateKeyPassword;

    @Value("${solace.jms.ssl.truststore}")
    private String trustStore;

    @Value("${solace.jms.ssl.truststore.password}")
    private String trustStorePassword;

    @Bean
    public JndiTemplate jndiTemplate() {
        Properties props = new Properties();
        props.put(InitialContext.PROVIDER_URL, url);
        props.put(InitialContext.INITIAL_CONTEXT_FACTORY, SolJNDIInitialContextFactory.class.getName());
        props.put(InitialContext.SECURITY_PRINCIPAL, username);
        props.put(SupportedProperty.SOLACE_JMS_VPN, vpn);
        props.put(SupportedProperty.SOLACE_JMS_SSL_VALIDATE_CERTIFICATE, true);
        props.put(SupportedProperty.SOLACE_JMS_AUTHENTICATION_SCHEME, authenticationScheme);
        props.put(SupportedProperty.SOLACE_JMS_SSL_KEY_STORE, keystore);
        props.put(SupportedProperty.SOLACE_JMS_SSL_KEY_STORE_PASSWORD, keystorePassword);
        props.put(SupportedProperty.SOLACE_JMS_SSL_PRIVATE_KEY_ALIAS, privateKeyAlias);
        props.put(SupportedProperty.SOLACE_JMS_SSL_PRIVATE_KEY_PASSWORD, privateKeyPassword);
        props.put(SupportedProperty.SOLACE_JMS_SSL_TRUST_STORE, trustStore);
        props.put(SupportedProperty.SOLACE_JMS_SSL_TRUST_STORE_PASSWORD, trustStorePassword);

        JndiTemplate jndi = new JndiTemplate();
        jndi.setEnvironment(props);

        return jndi;
    }

}
