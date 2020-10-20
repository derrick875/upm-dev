package com.nets.nps.paynow.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class GracefulShutdownConfiguration {

    @Value("${server.gracefulshutdown.timeout.second:50}")
    private long timeoutInSec;

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown(timeoutInSec);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
            @Override
            public void customize(TomcatServletWebServerFactory factory) {
                factory.addConnectorCustomizers(gracefulShutdown());
            }
        };
    }

    private static class GracefulShutdown implements TomcatConnectorCustomizer {

        private static final Logger logger = LoggerFactory.getLogger(GracefulShutdown.class);

        private volatile long timeoutInSec;
        private volatile Connector connector;

        public GracefulShutdown(long timeoutInSec) {
            this.timeoutInSec = timeoutInSec;
        }

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }

        @EventListener(ContextClosedEvent.class)
        public void onApplicationEvent(ContextClosedEvent event) {
            logger.info("shutting down the service.");

            this.connector.pause();

            Executor executor = this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                try {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(timeoutInSec, TimeUnit.SECONDS)) {
                        logger.warn("Tomcat thread pool did not shut down gracefully within "
                                    + timeoutInSec + " seconds. Proceeding with forceful shutdown");

                        threadPoolExecutor.shutdownNow();

                        if (!threadPoolExecutor.awaitTermination(timeoutInSec, TimeUnit.SECONDS)) {
                            logger.error("Failed to terminate Tomcat thread pool.");
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }

}
