package com.fcwenergy.iotdb.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Slf4j
@Component
@Getter
@Setter
@PropertySource("classpath:iotdb-datasource.properties")
@ConfigurationProperties(prefix = "spring.datasource.iotdb")
public class IotDbConfigProperties {
    private int port;

    private String host;

    private String username;

    private String password;

    private int maxActive;

    private int maxWait;

    private long waitToGetSessionTimeoutInMs = 10000;

    private int connectionTimeoutInMs = 5000;
}
