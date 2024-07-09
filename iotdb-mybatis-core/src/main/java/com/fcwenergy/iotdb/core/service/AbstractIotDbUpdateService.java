package com.fcwenergy.iotdb.core.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fcwenergy.common.domain.param.BaseQueryParam;
import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.base.CountNumDTO;
import com.fcwenergy.iotdb.config.IotDbSessionApiConfig;
import com.fcwenergy.iotdb.core.mapper.IotDbBaseMapper;
import com.fcwenergy.iotdb.core.service.inject.DataFieldInfo;
import com.fcwenergy.iotdb.core.util.IotDbDataFieldInfoUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Slf4j
public abstract class AbstractIotDbUpdateService<T> {
    protected static final int MAX_QUERY_COUNT = 10000;

    @Setter
    @Getter
    protected IotDbSessionApiConfig sessionApiConfig;

    public int insert(BigdataBaseParam baseParam, T param, Long timestamp) {
        String deviceId = baseParam.getPath();
        DataFieldInfo dataFieldInfo = IotDbDataFieldInfoUtil.generateFieldInfo(param);
        if (CollUtil.isEmpty(dataFieldInfo.getFields())) {
            return 0;
        }
        //总有一个时间戳是可用的
        long ts = ObjectUtil.defaultIfNull(timestamp, ObjectUtil.defaultIfNull(dataFieldInfo.getTimeStamp(), System.currentTimeMillis()));
        try {
            sessionApiConfig.getSessionPool().insertRecord(deviceId, ts, dataFieldInfo.getFields(), dataFieldInfo.getTypes(), dataFieldInfo.getValues());
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.warn("insertRecord error:{}", e.getMessage());
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public int insertBatch(List<BigdataBaseParam> baseParamList, List<T> params) {
        if (CollUtil.isEmpty(params)) {
            return 0;
        }
        if (CollUtil.isEmpty(baseParamList) || baseParamList.size() != params.size()) {
            log.warn("insertBatch error: baseParamList size not equals params size");
        }
        List<DataFieldInfo> dataFieldInfoList = CollUtil.newArrayList();
        for (T entity : params) {
            dataFieldInfoList.add(IotDbDataFieldInfoUtil.generateFieldInfo(entity));
        }
        if (CollUtil.isEmpty(dataFieldInfoList)) {
            return 0;
        }
        List<String> deviceIds = CollUtil.newArrayList();
        List<Long> times = CollUtil.newArrayList();
        List<List<String>> measurementsList = CollUtil.newArrayList();
        List<List<TSDataType>> typesList = CollUtil.newArrayList();
        List<List<Object>> valuesList = CollUtil.newArrayList();
        for (int i = 0; i < baseParamList.size(); i++) {
            BigdataBaseParam baseParam = baseParamList.get(i);
            DataFieldInfo dataFieldInfo = dataFieldInfoList.get(i);
            if (CollUtil.isEmpty(dataFieldInfo.getFields())) {
                continue;
            }
            deviceIds.add(baseParam.getPath());
            times.add(ObjectUtil.defaultIfNull(dataFieldInfo.getTimeStamp(), System.currentTimeMillis()));
            measurementsList.add(dataFieldInfo.getFields());
            typesList.add(dataFieldInfo.getTypes());
            valuesList.add(dataFieldInfo.getValues());
        }
        try {
            sessionApiConfig.getSessionPool().insertRecords(deviceIds, times, measurementsList, typesList, valuesList);
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.warn("insertRecord error:{}", e.getMessage());
            return 0;
        }
        return dataFieldInfoList.size();
    }

    /**
     * iot多设备只支持分组统计，这里处理为分组后二次求统计之和
     */
    protected <Q extends BaseQueryParam> int queryCount(IotDbBaseMapper<T, Q> mapper, BigdataBaseParam baseParam, Q query) {
        if (CollUtil.isEmpty(query.getCreateTime())) {
            throw new IllegalArgumentException("时序库查询请先加入时间范围条件");
        }
        baseParam.setCreateTime(query.getCreateTime());
        List<CountNumDTO> countDataList = mapper.queryCountList(baseParam, query);
        List<Integer> countList = countDataList.stream()
                                               .map(CountNumDTO::getCount)
                                               .collect(Collectors.toList());
        return CollUtil.isNotEmpty(countList) ? countList.stream()
                                                         .mapToInt(Integer::intValue)
                                                         .sum() : 0;
    }
}
