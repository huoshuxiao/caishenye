package com.sun.caishenye.octopus.config.rest;

import com.sun.caishenye.octopus.config.rest.impl.ManualHumanInteractionHandler;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OkHttpClientConfig {

    @Value("${okhttp.retries}")
    private int retry;

    @Value("${okhttp.log.level:NONE}")
    private String okHttpLogLevel;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .addInterceptor(new OkHttpClientRetryInterceptor(retry, new ManualHumanInteractionHandler(10))) // 设置最多重试 3 次
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.valueOf(okHttpLogLevel)))
                .build();
    }
}

