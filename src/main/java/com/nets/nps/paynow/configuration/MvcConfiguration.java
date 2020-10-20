package com.nets.nps.paynow.configuration;

import com.nets.nps.paynow.filter.HSMFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MvcConfiguration {

    @Bean
    public FilterRegistrationBean<HSMFilter> loggingFilter(){
        FilterRegistrationBean<HSMFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new HSMFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

}
