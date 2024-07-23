package com.fcwenergy.iotdb.dts.core;

import com.fcwenergy.iotdb.dts.config.IotDbDTSProperties;
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
 * dts iotdb session api config
 *
 * @author endcy
 * @date 2024/07/22 23:30:30
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class DTSIotDbSessionApiConfig implements InitializingBean {

    private final IotDbDTSProperties iotDbDTSProperties;

    @Getter
    public SessionPool sessionPool;

    private synchronized void constructCustomSessionPool() {
        this.sessionPool = new SessionPool.Builder()
                .host(iotDbDTSProperties.getHost())
                .port(iotDbDTSProperties.getPort())
                .user(iotDbDTSProperties.getUsername())
                .password(iotDbDTSProperties.getPassword())
                .maxSize(iotDbDTSProperties.getMaxActive())
                .timeOut(60000)
                .connectionTimeoutInMs(5000)
                .waitToGetSessionTimeoutInMs(10000)
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
