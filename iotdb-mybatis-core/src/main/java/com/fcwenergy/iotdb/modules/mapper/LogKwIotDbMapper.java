package com.fcwenergy.iotdb.modules.mapper;

import com.fcwenergy.common.domain.entity.LogKw;
import com.fcwenergy.common.domain.param.LogKwQueryParam;
import com.fcwenergy.iotdb.core.mapper.IotDbBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Mapper
public interface LogKwIotDbMapper extends IotDbBaseMapper<LogKw, LogKwQueryParam> {

}

