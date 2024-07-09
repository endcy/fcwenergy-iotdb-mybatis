package com.fcwenergy.iotdb.config;

import lombok.Getter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Configuration
@ComponentScan("com.fcwenergy.iotdb")
@Getter
public class IotDbContextConfiguration {

    /**
     * 服务id，不同机器或同集群中相同服务 id不同，可用环境变量或强制配置传参
     */
    private final String serviceId = "iotdb";

}
