package com.credit.engine.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@Slf4j
public class GlobalErrorHandling {

    //REST API Error Handling
    @Bean
    public IntegrationFlow globalErrorChannel(){
        return f -> f.<RuntimeException, Message<?>>transform(payload -> {
            log.error("APi Gateway Error : {}", payload);
            if (payload.getCause() instanceof ApiRuntimeException) {
                ApiRuntimeException apiRuntimeException = (ApiRuntimeException)payload.getCause();
                return org.springframework.integration.support.MessageBuilder
                        .withPayload(apiRuntimeException.getErrorResponse())
                        .setHeaderIfAbsent(HttpHeaders.STATUS_CODE, apiRuntimeException.getHttpStatus())
                        .build();
            }
            return MessageBuilder
                    .withPayload(Strings.EMPTY)
                    .setHeaderIfAbsent(HttpHeaders.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        });
    }
}
