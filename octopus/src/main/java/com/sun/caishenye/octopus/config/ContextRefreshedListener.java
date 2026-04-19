package com.sun.caishenye.octopus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 命令行参数
 * spring容器初始化完毕后，加载全局配置。
 */
@Configuration
@Slf4j
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>, CommandLineRunner {

//    @Autowired
//    private CacheComponent cache;
//
//    @Value("${file.path}")
//    private String filePath;
//
//    @Value("${job.run:false}")
//    private boolean run;

    @Autowired
    private Environment env;

    private static final Set<String> SENSITIVE_KEYS = Arrays.stream(
            "password,secret,key,token,credentials,auth,passphrase,access,signature,cookie".split(",")
    ).collect(Collectors.toSet());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("① 自定义初始化开始.............");
        outEnv();
        log.info("① 自定义初始化完毕.............");
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("② 自定义初始化开始 :: 开始命令行参数.............");
//        outEnv();
//        String filePath = this.filePath;
//        if (args.length > 0) {
//            filePath = args[0];
//            StringTokenizer tokenizer = new StringTokenizer(filePath, "=");
//            while (tokenizer.hasMoreTokens()) {
//                // key
//                tokenizer.nextToken();
//                // value
//                filePath = cache.putIfAbsentFilePath(tokenizer.nextToken());
//                log.info("自定义设置 :: command filePath >> {}", filePath);
//            }
//        } else {
//            cache.putIfAbsentFilePath(filePath);
//            log.info("自定义设置 :: default filePath >> {}", filePath);
//        }
        log.debug("② 自定义初始化完毕 :: 开始命令行参数.............");
    }

    private void outEnv() {
//        String[] sources = ((AbstractEnvironment) env).getPropertySources()
//                .stream()
//                .filter(ps -> ps.containsProperty("job.run"))
//                .map(PropertySource::getName)
//                .toArray(String[]::new);
//        log.info("配置来源: {}", Arrays.toString(sources));

        if (!(env instanceof ConfigurableEnvironment)) return;

        int fileCount = 0;
        int totalProps = 0;
        int maskedCount = 0;

        for (PropertySource<?> ps : ((ConfigurableEnvironment) env).getPropertySources()) {
            // 【精准过滤】仅处理配置文件来源（Spring Boot 2.2.5 标准命名）
            if (!ps.getName().contains("applicationConfig")) continue;

            if (ps instanceof EnumerablePropertySource) {
                String[] names = ((EnumerablePropertySource<?>) ps).getPropertyNames();
                if (names.length == 0) continue;

                fileCount++;
                log.debug("📁 配置文件: {}", extractFileName(ps.getName()));

                Arrays.sort(names); // 按字母排序，便于查阅
                for (String key : names) {
                    totalProps++;
                    String value = maskIfSensitive(key, ps.getProperty(key));
                    if (value.equals("******")) maskedCount++;
                    log.debug("  {} = {}", key, value);
                }
            }
        }

        log.debug("✅ 统计: 配置文件数={} | 属性总数={} | 脱敏字段={}",
                fileCount, totalProps, maskedCount);
        log.info("🔍 重点验证: job.run = {}", env.getProperty("job.run", "NOT SET"));
        log.info("🔍 重点验证: mf.sh = {}", env.getProperty("mf.sh", "NOT SET"));
    }

    /** 提取文件名（如: file:/home/test1/config/application.yml → application.yml） */
    private String extractFileName(String sourceName) {
        int lastSlash = sourceName.lastIndexOf('/');
        return (lastSlash != -1) ? sourceName.substring(lastSlash + 1).replace("]", "")
                : sourceName.replace("applicationConfig: [", "").replace("]", "");
    }

    /** 敏感字段脱敏 */
    private String maskIfSensitive(String key, Object value) {
        if (value == null) return "null";
        String lowerKey = key.toLowerCase().replaceAll("[-_]", "");
        return SENSITIVE_KEYS.stream().anyMatch(lowerKey::contains)
                ? "******"
                : value.toString();
    }
}
