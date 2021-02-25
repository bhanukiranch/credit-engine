package com.credit.engine.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {
    static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private boolean ignoreTraceError = false;
    private boolean printBodyRequest = true;
    private boolean printHeadersRequest = true;

    public RequestLoggingInterceptor() {
    }

    public RequestLoggingInterceptor(boolean ignoreTraceError) {
        this.ignoreTraceError = ignoreTraceError;
    }

    public RequestLoggingInterceptor(boolean ignoreTraceError, boolean printHeadersRequest, boolean printBodyRequest) {
        this.ignoreTraceError = ignoreTraceError;
        this.printHeadersRequest = printHeadersRequest;
        this.printBodyRequest = printBodyRequest;
    }

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("===========================request begin================================================");
        log.info("URI         :: {}", request.getURI());
        log.info("Method      :: {}", request.getMethod());
        if (this.printHeadersRequest) {
            log.info("Headers     :: {}", request.getHeaders());
        }

        if (this.printBodyRequest) {
            log.info("Request body :: {}", new String(body, "UTF-8"));
        }

        log.info("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"))) {
                    StringBuilder inputStringBuilder = new StringBuilder();
                    String line = bufferedReader.readLine();

                    while(line == null) {
                        inputStringBuilder.append(line);
                        inputStringBuilder.append('\n');
                        line = bufferedReader.readLine();
                    }
                    log.info("============================response begin==========================================");
                    log.info("Status code   :: {}", response.getStatusCode());
                    log.info("Status text   :: {}", response.getStatusText());
                    log.info("Headers       :: {}", response.getHeaders());
                    log.info("Response body :: {}", inputStringBuilder.toString());
                    log.info("=======================response end=================================================");

        } catch (IOException e) {
            log.error("Error in traceResponse ::", e);
            if (!ignoreTraceError) {
                throw e;
            }
        }

    }

    public void setIgnoreTraceError(boolean ignoreTraceError) {
        this.ignoreTraceError = ignoreTraceError;
    }

    public void setPrintBodyRequest(boolean printBodyRequest) {
        this.printBodyRequest = printBodyRequest;
    }

    public void setPrintHeadersRequest(boolean printHeadersRequest) {
        this.printHeadersRequest = printHeadersRequest;
    }
}