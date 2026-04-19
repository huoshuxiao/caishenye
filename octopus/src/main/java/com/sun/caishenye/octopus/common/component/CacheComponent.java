package com.sun.caishenye.octopus.common.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheComponent {

    @Value("${file.path}")
    private String filePath;

    @Cacheable(value = "ehcache_10M")
    public String getBaseFilePath() {
        log.info("自定义设置 :: file Path >> {}", this.filePath);
        return this.filePath;
    }
}
