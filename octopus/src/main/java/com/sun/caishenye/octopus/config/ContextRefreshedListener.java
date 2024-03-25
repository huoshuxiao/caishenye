package com.sun.caishenye.octopus.config;

import com.sun.caishenye.octopus.common.Initializations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * spring容器初始化完毕后，加载全局配置。
 */
@Component
@Slf4j
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Initializations init;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("spring容器初始化完毕.............");

        log.info("自定义初始化开始.............");
        init.init();
        log.info("自定义初始化完毕.............");
    }
}
