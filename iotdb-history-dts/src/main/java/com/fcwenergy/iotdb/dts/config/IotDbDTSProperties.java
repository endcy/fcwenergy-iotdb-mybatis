package com.fcwenergy.iotdb.dts.config;

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
@PropertySource("classpath:dts-iotdb-datasource.properties")
@ConfigurationProperties(prefix = "spring.datasource.dts.iotdb")
public class IotDbDTSProperties {
    private int port;

    private String host;

    private String username;

    private String password;

    private int maxActive;

    private int minuteTimeStep;
}
