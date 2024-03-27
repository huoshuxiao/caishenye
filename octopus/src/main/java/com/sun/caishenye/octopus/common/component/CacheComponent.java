package com.sun.caishenye.octopus.common.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheComponent {

    @Getter
    @Setter
    private String filePath;
}
