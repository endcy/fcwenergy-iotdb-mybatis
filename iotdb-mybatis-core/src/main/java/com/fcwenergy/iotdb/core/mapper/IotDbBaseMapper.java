package com.fcwenergy.iotdb.core.mapper;

import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.base.CountNumDTO;
import com.fcwenergy.iotdb.core.annotation.GenerateTablePath;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 注意，这里没有批量插入，也就是所有插入数据都是近乎实时操作，不需要额外定义新增记录的时序参数
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Repository
public interface IotDbBaseMapper<T, Q> {

    @GenerateTablePath
    List<T> queryList(BigdataBaseParam baseParam, Q query);

    @GenerateTablePath
    List<T> queryPage(BigdataBaseParam baseParam, Q param, int pageSize, long offset);

    @GenerateTablePath
    Integer queryCount(BigdataBaseParam baseParam, Q param);

    List<CountNumDTO> queryCountList(BigdataBaseParam baseParam, Q param);

    @GenerateTablePath
    Integer delete(BigdataBaseParam baseParam, List<Date> createTime);

}

