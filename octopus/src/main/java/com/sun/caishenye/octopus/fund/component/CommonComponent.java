package com.sun.caishenye.octopus.fund.component;

import com.sun.caishenye.octopus.common.component.CacheComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Slf4j
@Component
public class CommonComponent {

    @Autowired
    private CacheComponent cache;

    public String getFilePath() {
        return Paths.get(cache.getBaseFilePath(), "fund").toString();
    }
}
