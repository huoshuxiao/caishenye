package com.sun.caishenye.octopus.stock.agent.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OkHttpClient 采集 (call api)
 */
@Component
@Slf4j
public class ApiOkHttpClient {

    @Autowired
    private OkHttpClient okHttpClient;

    public String call(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("HTTP error code: " + response.code());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
