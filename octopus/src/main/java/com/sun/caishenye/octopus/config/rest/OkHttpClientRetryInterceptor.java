package com.sun.caishenye.octopus.config.rest;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class OkHttpClientRetryInterceptor implements Interceptor {

    private final int maxRetry; // 最大重试次数

    public OkHttpClientRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int tryCount = 0;
        IOException lastException = null;

        while (tryCount <= maxRetry) {
            try {
                log.info("尝试第 {} 次请求: {}", tryCount + 1, request.url());
                Response response = chain.proceed(request);
                // HTTP 非 2xx 成功码，触发重试
                if (!response.isSuccessful()) {
                    int code = response.code();
                    String msg = response.message();
                    // ✅ 先关闭连接，再抛异常
                    response.close();
//                    throw new IOException("HTTP error code: " + response.code());
                    throw new IOException(String.format("HTTP %d: %s (URL: %s)", code, msg, request.url()));
                }
                return response;
            } catch (IOException e) {
                lastException = e;
                tryCount++;

                if (tryCount > maxRetry) {
                    log.error("已达到最大重试次数，放弃请求: {}", request.url(), e);
                    break;
                }

                log.warn("请求失败，准备第 {} 次重试: {}", tryCount, request.url());

                // 加入延迟策略（例如指数退避）
                try {
                    Thread.sleep((long) Math.pow(2, tryCount) * 1000); // 1s, 2s, 4s...
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // 重新设置中断状态
                    throw new IOException("重试过程被中断", ie);
                }
            }
        }

        // 抛出最后一次异常
        throw lastException != null ? lastException : new IOException("未知网络错误");
    }
}
