package com.nets.nps.paynow.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.nets.upos.core.repository", "com.nets.nps.upi.repository" })
@EntityScan(basePackages = { "com.nets.upos.core.entity", "com.nets.nps.upi.entity" })
public class DefaultJPAConfiguration { }
