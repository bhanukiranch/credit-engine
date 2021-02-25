package com.credit.engine;

import com.credit.engine.api.logging.RequestLoggingInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    @ConfigurationProperties(prefix = "app.rest.http")
    public SimpleClientHttpRequestFactory restHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate =
                new RestTemplate(new BufferingClientHttpRequestFactory(restHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new RequestLoggingInterceptor(true, true, true));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
