package com.sun.caishenye.octopus.stock.agent.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
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
        return call(request, url);
    }

    public String call(String url, String header, String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader(header, value)
                .build();
        return call(request, url);
    }

    private String call(Request request, String url) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("HTTP请求失败 [{}]: {} {}", url, response.code(), response.message());
                return "";
            }
            ResponseBody body = response.body();
            return (body != null) ? body.string() : "";
        } catch (IOException ex) {
            // 【关键】捕获所有IO异常（网络超时/连接失败等）
            log.error("HTTP请求异常 [{}]: {}", url, ex.getMessage(), ex);
//            throw new RuntimeException(ex);
            return "";
        }
    }
}
