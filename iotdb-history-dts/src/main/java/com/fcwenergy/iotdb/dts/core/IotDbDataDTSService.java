package com.fcwenergy.iotdb.dts.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fcwenergy.common.domain.param.BaseQueryParam;
import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.core.service.IotDbBaseService;
import com.fcwenergy.iotdb.core.service.inject.DataFieldInfo;
import com.fcwenergy.iotdb.core.util.IotDbDataFieldInfoUtil;
import com.fcwenergy.iotdb.dts.config.IotDbDTSProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * iotdb数据dts备份
 *
 * @author endcy
 * @date 2024/07/20 18:30:30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IotDbDataDTSService<Q extends BaseQueryParam, T> {

    private final DTSIotDbSessionApiConfig dtsIotDbSessionApiConfig;
    private final IotDbDTSProperties iotDbDTSProperties;

    /**
     * @param service    IotDbBaseService实现类
     * @param queryParam 基础查询参数，时间参数会被覆盖
     * @param function   获取base数据的函数
     * @param startTime  所有时序开始时间
     * @param endTime    所有时序结束时间
     */
    public void dataTransformBackUp(IotDbBaseService<T, Q> service, Q queryParam, IBaseParamFunction<T> function, Date startTime, Date endTime) {
        Date startQueryTime = startTime;
        int queryTimes = 0;
        int errorTimes = 0;
        while (DateUtil.compare(startQueryTime, endTime) < 0) {
            if (errorTimes > 10) {
                log.error("insertData error times:{}, query:{}", errorTimes, queryParam);
                return;
            }
            try {
                //步进5分钟赋值startTime
                startQueryTime = queryTimes != 0 ? DateUtil.offsetMinute(startQueryTime, iotDbDTSProperties.getMinuteTimeStep()) : startTime;
                Date endQueryTime = DateUtil.offsetMinute(startQueryTime, iotDbDTSProperties.getMinuteTimeStep());
                //覆盖查询时间
                queryParam.setCreateTime(CollUtil.newArrayList(startQueryTime, endQueryTime));
                List<T> dataList = queryInterval(service, queryParam);
                dataTransformBackUp(dataList, function);
                queryTimes++;
            } catch (Exception e) {
                errorTimes++;
                log.error("insertData error", e);
            }
        }
    }


    /**
     * 在改方法中开始执行time循环查询、插入数据，设定一个设备一个线程
     *
     * @param service    .
     * @param queryParam .
     * @return .
     */
    private List<T> queryInterval(IotDbBaseService<T, Q> service, Q queryParam) {
        //最多一次查询10万条数据，需要充分把握queryParam中的时间参数
        List<T> resultList = service.queryAll(queryParam);
        if (CollUtil.isEmpty(resultList)) {
            return CollUtil.newArrayList();
        }
        return resultList;
    }

    /**
     * 使用原方法查询列表数据
     *
     * @param dataList .
     * @param function .
     */
    private void dataTransformBackUp(List<T> dataList, IBaseParamFunction<T> function) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        List<BigdataBaseParam> baseParamList = CollUtil.newArrayList();
        BigdataBaseParam baseParam = null;
        for (T data : dataList) {
            baseParam = function.apply(data);
            baseParamList.add(baseParam);
        }
        int insertCount = insertBatch(baseParamList, dataList);
        if (baseParam != null) {
            log.info("insertData success:{}, baseParam type:{}, queryParam Time:{}", insertCount, baseParam.getCategory(), baseParamList.get(0).getCreateTime());
        }
    }

    private int insertBatch(List<BigdataBaseParam> baseParamList, List<T> params) {
        if (CollUtil.isEmpty(params) || CollUtil.isEmpty(baseParamList)) {
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
            dtsIotDbSessionApiConfig.getSessionPool().insertRecords(deviceIds, times, measurementsList, typesList, valuesList);
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.warn("insertRecord error", e);
            log.info("startTime:{}, endTime:{}", times.get(0), times.get(times.size() - 1));
            return 0;
        }
        return dataFieldInfoList.size();
    }

}
