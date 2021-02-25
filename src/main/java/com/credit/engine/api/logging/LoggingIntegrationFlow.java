package com.credit.engine.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.HttpHeaders;

@Configuration
@Slf4j
public class LoggingIntegrationFlow {

    @Bean
    public IntegrationFlow loggingFlow() {
        return f -> f.handle(message -> {
            log.info("===========================incoming request details================================================");
            log.info("URI         : {}", message.getHeaders().get(HttpHeaders.REQUEST_URL));
            log.info("Method      : {}", message.getHeaders().get(HttpHeaders.REQUEST_METHOD));
            log.info("Request body: {}", message.getPayload());
            log.info("===================================================================================================");
        });
    }
}
