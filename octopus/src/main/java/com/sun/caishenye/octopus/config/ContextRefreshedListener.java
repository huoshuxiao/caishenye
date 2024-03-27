package com.sun.caishenye.octopus.config;

import com.sun.caishenye.octopus.common.component.CacheComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.StringTokenizer;

/**
 * spring容器初始化完毕后，加载全局配置。
 */
@Configuration
@Slf4j
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>, CommandLineRunner {

    @Autowired
    private CacheComponent cache;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("自定义初始化开始.............");
        log.info("自定义初始化完毕.............");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("自定义初始化开始 :: 开始命令行参数.............");
        String filePath = args[0];
        StringTokenizer tokenizer = new StringTokenizer(filePath, "=");
        while (tokenizer.hasMoreTokens()) {
            // key
            tokenizer.nextToken();
            // value
            cache.setFilePath(tokenizer.nextToken());
        }

        log.info(cache.getFilePath());
        log.info("自定义初始化完毕 :: 开始命令行参数.............");
    }
}
