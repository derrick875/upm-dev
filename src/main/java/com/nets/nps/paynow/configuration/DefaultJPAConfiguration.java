package com.nets.nps.paynow.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.nets.upos.core.repository" })
@EntityScan(basePackages = { "com.nets.upos.core.entity" })
public class DefaultJPAConfiguration { }
