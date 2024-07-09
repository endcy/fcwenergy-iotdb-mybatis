package com.fcwenergy.iotdb.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class IotDbSessionApiConfig implements InitializingBean {
    private final IotDbConfigProperties iotDbConfigProperties;

    @Getter
    public SessionPool sessionPool;

    private synchronized void constructCustomSessionPool() {
        this.sessionPool = new SessionPool.Builder()
                .host(iotDbConfigProperties.getHost())
                .port(iotDbConfigProperties.getPort())
                .user(iotDbConfigProperties.getUsername())
                .password(iotDbConfigProperties.getPassword())
                .maxSize(iotDbConfigProperties.getMaxActive())
//                .enableCompression(true)
                .timeOut(iotDbConfigProperties.getMaxWait())
                .connectionTimeoutInMs(iotDbConfigProperties.getConnectionTimeoutInMs())
                .waitToGetSessionTimeoutInMs(iotDbConfigProperties.getWaitToGetSessionTimeoutInMs())
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        constructCustomSessionPool();
        if (this.sessionPool == null) {
            throw new SqlSessionException("constructCustomSessionPool error!");
        }
    }
}
