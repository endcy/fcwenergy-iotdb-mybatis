package com.fcwenergy.iotdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Configuration
public class CommonServiceBeanConfig {

    /**
     * 用于执行异步业务的公共线程池
     * 使用方法： @Async("commonTaskExecutor")
     */
    @Bean("commonTaskExecutor")
    public Executor commonTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix("execService-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 用于执行异步持久化处理的公共线程池
     * 使用方法： @Async("dbTaskExecutor")
     */
    @Bean("dbTaskExecutor")
    public Executor dbTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix("asyncDbService-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

}
