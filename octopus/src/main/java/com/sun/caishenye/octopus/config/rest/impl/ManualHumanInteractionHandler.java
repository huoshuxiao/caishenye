package com.sun.caishenye.octopus.config.rest.impl;

import com.sun.caishenye.octopus.config.rest.HumanInteractionHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ManualHumanInteractionHandler implements HumanInteractionHandler {

    private final long awaitHours;

    public ManualHumanInteractionHandler(long awaitHours) {
        this.awaitHours = awaitHours;
    }

    @Override
    public Request handle(Response response, Request originalRequest) {
        String body = null;
        try (ResponseBody rb = response.peekBody(4096)) {
            body = rb.string();
        } catch (IOException ignored) {
        }

        // 1. 保存验证码页面，方便人工查看
        String captchaFile = saveCaptchaPage(body, originalRequest.url().toString());
        log.info("============================================");
        log.info("需要人工验证，请打开页面后继续：");
        log.info(captchaFile);
        log.info("完成后请输入任意内容继续，超时将放弃请求。");
        log.info("============================================");

        // 2. 阻塞等待人工输入
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> input = new AtomicReference<>();

        Thread waitThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                input.set(scanner.nextLine());
            } finally {
                latch.countDown();
            }
        }, "captcha-wait");
        waitThread.setDaemon(true);
        waitThread.start();

        try {
            boolean completed = latch.await(awaitHours, TimeUnit.HOURS);
            if (!completed) {
                log.warn("人工验证超时，放弃请求: {}", originalRequest.url());
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待人工验证被中断", e);
        }

        // 3. 人工确认后，重新发起请求
        // 很多站点验证完成后，原 Cookie / Session 已恢复，直接重试即可
        return originalRequest.newBuilder().build();
    }

    private String saveCaptchaPage(String body, String url) {
        try {
            File dir = new File("captcha");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = "captcha_" + System.currentTimeMillis() + ".html";
            File file = new File(dir, fileName);

            String content = "<!-- URL: " + url + " -->\n"
                    + (body != null ? body : "");

            Files.write(
                    file.toPath(),
                    content.getBytes(StandardCharsets.UTF_8)
            );

            return file.getAbsolutePath();
        } catch (IOException e) {
            log.error("保存验证码页面失败", e);
            return url;
        }
    }
}
