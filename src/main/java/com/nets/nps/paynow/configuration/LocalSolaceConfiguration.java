package com.nets.nps.paynow.configuration;

import java.util.Properties;

import javax.naming.InitialContext;

import com.solacesystems.jms.SupportedProperty;

import com.solacesystems.jndi.SolJNDIInitialContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiTemplate;

@Configuration
@Profile("local")
public class LocalSolaceConfiguration {

    @Value("${solace.url}")
    private String url;

    @Value("${solace.username}")
    private String username;

    @Value("${solace.vpn}")
    private String vpn;

    @Value("${solace.password}")
    private String password;

    @Bean
    public JndiTemplate jndiTemplate() {
        Properties props = new Properties();
        props.put(InitialContext.PROVIDER_URL, url);
        props.put(InitialContext.INITIAL_CONTEXT_FACTORY, SolJNDIInitialContextFactory.class.getName());
        props.put(InitialContext.SECURITY_PRINCIPAL, username);
        props.put(InitialContext.SECURITY_CREDENTIALS, password);
        props.put(SupportedProperty.SOLACE_JMS_VPN, vpn);

        JndiTemplate jndi = new JndiTemplate();
        jndi.setEnvironment(props);

        return jndi;
    }
}
