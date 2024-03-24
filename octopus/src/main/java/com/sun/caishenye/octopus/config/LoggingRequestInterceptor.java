package com.sun.caishenye.octopus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        log.debug("===========================①request begin================================================");
        log.info("Request URI   : {}", request.getURI());
        log.debug("Method       : {}", request.getMethod());
        log.debug("Headers      : {}", request.getHeaders());
        log.debug("Request Body : {}", new String(body, StandardCharsets.UTF_8));
        log.debug("===========================①request end==================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
        String line = bufferedReader.readLine();

        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }

        log.debug("============================②response begin=========================================");
        log.info("Response Status Code    : {}", response.getStatusCode());
        log.debug("Status Text            : {}", response.getStatusText());
        log.debug("Headers                : {}", response.getHeaders());
        log.debug("Response Body          : {}", inputStringBuilder);
        log.debug("============================②response end===========================================");
    }
}