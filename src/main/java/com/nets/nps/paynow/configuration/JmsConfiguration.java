package com.nets.nps.paynow.configuration;

import javax.jms.ConnectionFactory;

import com.nets.nps.paynow.exception.JmsExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableJms
public class JmsConfiguration {

    @Value("${solace.connection.factory}")
    private String jndiName;

    @Value("${jms.concurrent.consumers.min}")
    private int concurrentConsumersMin;

    @Value("${jms.concurrent.consumers.max}")
    private int concurrentConsumersMax;

    @Value("${jms.concurrent.executor.queue.capacity}")
    private int queueCapacity;

    @Value("${jms.receive.timeout}")
    private long receiveTimeout;

    @Value("${jms.listener.gracefulshutdown.timeout.second:20}")
    private int timeoutInSec;

    @Bean
    public JndiObjectFactoryBean connectionFactory(JndiTemplate jndiTemplate) {
        final JndiObjectFactoryBean jndiFactory = new JndiObjectFactoryBean();
        jndiFactory.setJndiTemplate(jndiTemplate);
        jndiFactory.setJndiName(jndiName);
        jndiFactory.setProxyInterface(ConnectionFactory.class);

        return jndiFactory;
    }

    @Bean
    public CachingConnectionFactory cachedConnectionFactoryfinal(ConnectionFactory connectionFactory) {
        final CachingConnectionFactory cachedConnectionFactory = new CachingConnectionFactory();
        cachedConnectionFactory.setTargetConnectionFactory(connectionFactory);
        // TODO make 10 a param
        cachedConnectionFactory.setSessionCacheSize(10);
        cachedConnectionFactory.setReconnectOnException(true);
        cachedConnectionFactory.setCacheConsumers(false);

        return cachedConnectionFactory;
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        final MappingJackson2MessageConverter jackson2MessageConverter = new MappingJackson2MessageConverter();

        jackson2MessageConverter.setTargetType(MessageType.TEXT);

        return jackson2MessageConverter;
    }

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory connectionFactory, MessageConverter jackson2MessageConverter) {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.setMessageConverter(jackson2MessageConverter);
        jmsTemplate.setReceiveTimeout(receiveTimeout);

        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(CachingConnectionFactory connectionFactory,
                                                                          MessageConverter jackson2MessageConverter,
                                                                          TaskExecutor defaultJmsExecutor) {
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // TODO Add property for jms listener concurrency
        factory.setErrorHandler(new JmsExceptionHandler());
        factory.setConcurrency(concurrentConsumersMin + "-" + concurrentConsumersMax);
        factory.setReceiveTimeout(receiveTimeout);
        factory.setMessageConverter(jackson2MessageConverter);
        factory.setTaskExecutor(defaultJmsExecutor);

        return factory;
    }

    @Bean
    public TaskExecutor defaultJmsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // For graceful shutdown
        executor.setCorePoolSize(concurrentConsumersMin);
        executor.setMaxPoolSize(concurrentConsumersMax);
        executor.setQueueCapacity(queueCapacity);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(timeoutInSec);

        return executor;
    }

}
