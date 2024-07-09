package com.fcwenergy.iotdb.modules;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fcwenergy.common.domain.entity.LogKw;
import com.fcwenergy.common.domain.param.LogKwQueryParam;
import com.fcwenergy.iotdb.core.service.IotDbBaseService;

import java.util.List;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
public interface LogKwIotDbService extends IotDbBaseService<LogKw, LogKwQueryParam> {
    List<LogKw> queryAll(LogKwQueryParam queryParam);

    LogKw queryLast(LogKwQueryParam query);

    IPage<LogKw> queryPage(IPage<LogKw> pageParam, LogKwQueryParam queryParam);

}
