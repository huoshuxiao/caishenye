package com.sun.caishenye.octopus.common.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheComponent {

    private String filePath;

    @Cacheable(value = "putIfAbsentFilePath")
    public String putIfAbsentFilePath(String filePath) {
        if (StringUtils.isNotEmpty(filePath)) {
            this.filePath = filePath;
        }
        return this.filePath;
    }

    public String putIfAbsentFilePath() {
        return putIfAbsentFilePath("");
    }
}
