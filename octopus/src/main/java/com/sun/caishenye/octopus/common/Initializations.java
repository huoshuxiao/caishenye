package com.sun.caishenye.octopus.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 全局配置类。
 */
@Component
@Slf4j
public class Initializations {

    @Value("${file.path}")
    private String filePath;

    public void init() {

        if (StringUtils.isNotEmpty(filePath)) {
            Constants.FILE_PATH.setString(filePath);
        }
        log.info("file path   ::   {}", Constants.FILE_PATH.getString());
    }
}
