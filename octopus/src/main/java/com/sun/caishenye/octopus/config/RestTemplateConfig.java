package com.sun.caishenye.octopus.config;

import com.sun.caishenye.octopus.interceptor.LoggingRequestInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    // json
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        //Add the Jackson Message converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        // Note: here we are making this converter to process any kind of response,
        // not only application/*json, which is the default behaviour
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        messageConverters.add(converter);
        messageConverters.add(new StringHttpMessageConverter());

        RestTemplate restTemplate = restTemplateBuilder.messageConverters()
                .additionalMessageConverters(messageConverters)
                .additionalInterceptors(new LoggingRequestInterceptor())
                .build();

        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()));
        return restTemplate;
    }

    // string
    @Bean("restTemplateText")
    public RestTemplate restTemplateText(RestTemplateBuilder restTemplateBuilder) {

        RestTemplate restTemplate = restTemplateBuilder.messageConverters()
                .additionalInterceptors(new LoggingRequestInterceptor())
                .build();

        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()));
        return restTemplate;
    }
}
