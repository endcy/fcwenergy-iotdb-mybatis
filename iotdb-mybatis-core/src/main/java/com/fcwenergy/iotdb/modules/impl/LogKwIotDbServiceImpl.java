package com.fcwenergy.iotdb.modules.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fcwenergy.common.domain.base.PageInfo;
import com.fcwenergy.common.domain.entity.LogKw;
import com.fcwenergy.common.domain.param.LogKwQueryParam;
import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.base.DataCategoryEnum;
import com.fcwenergy.iotdb.config.IotDbSessionApiConfig;
import com.fcwenergy.iotdb.core.service.AbstractIotDbUpdateService;
import com.fcwenergy.iotdb.modules.LogKwIotDbService;
import com.fcwenergy.iotdb.modules.mapper.LogKwIotDbMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogKwIotDbServiceImpl extends AbstractIotDbUpdateService<LogKw> implements LogKwIotDbService {
    private final IotDbSessionApiConfig sessionApiConfig;
    private final LogKwIotDbMapper logKwIotDbMapper;

    @Override
    public void insert(LogKw logMsg, Long timestamp) {
        setSessionApiConfig(sessionApiConfig);
        BigdataBaseParam baseParam = BigdataBaseParam.builder().category(DataCategoryEnum.ES_KW_LOG).equipmentId(logMsg.getEquipmentId()).build();
        insert(baseParam, logMsg, timestamp);
    }

    @Override
    public PageInfo<LogKw> queryPage(LogKwQueryParam query, Pageable pageable) {
        if (CollUtil.isEmpty(query.getCreateTime())) {
            //默认一个月内数据
            List<Date> createTimes = CollUtil.newArrayList(DateUtil.date().offset(DateField.DAY_OF_MONTH, -1), DateUtil.date());
            query.setCreateTime(createTimes);
        }
        BigdataBaseParam baseParam = BigdataBaseParam.builder().category(DataCategoryEnum.ES_KW_LOG).isQuery(true).equipmentId(query.getEquipmentId()).build();
        int dataCount = queryCount(logKwIotDbMapper, baseParam, query);
        if (dataCount <= 0) {
            return new PageInfo<>(0, CollUtil.newArrayList());
        }
        List<LogKw> pageList = logKwIotDbMapper.queryPage(baseParam, query, pageable.getPageSize(), pageable.getOffset());
        return new PageInfo<>(dataCount, pageList);
    }

    public List<LogKw> queryAll(LogKwQueryParam query) {
        BigdataBaseParam baseParam = BigdataBaseParam.builder()
                                                     .category(DataCategoryEnum.ES_KW_LOG)
                                                     .isQuery(true)
                                                     .equipmentId(query.getEquipmentId())
                                                     .createTime(query.getCreateTime())
                                                     .limit(MAX_QUERY_COUNT)
                                                     .build();
        return logKwIotDbMapper.queryList(baseParam, query);
    }

    public LogKw queryLast(LogKwQueryParam query) {
        BigdataBaseParam baseParam = BigdataBaseParam.builder().category(DataCategoryEnum.ES_KW_LOG).equipmentId(query.getEquipmentId()).build();
        // 后续的大数据查询，必须强制暴露时间范围，xml中不做if判断
        if (CollUtil.isEmpty(query.getCreateTime())) {
            Date now = DateUtil.date();
            Date yesterday = DateUtil.yesterday();
            baseParam.setCreateTime(CollUtil.newArrayList(yesterday, now));
            baseParam.setLimit(1);
        }
        List<LogKw> list = logKwIotDbMapper.queryPage(baseParam, query, 1, 0);
        return CollUtil.isEmpty(list) ? null : list.get(0);
    }


    @Override
    @SuppressWarnings("all")
    public IPage<LogKw> queryPage(IPage<LogKw> pageParam, LogKwQueryParam query) {
        Pageable pageable = PageRequest.of((int) pageParam.getCurrent() - 1, (int) pageParam.getSize());
        PageInfo<LogKw> pageResult = queryPage(query, pageable);
        PageDTO<LogKw> page = new PageDTO<>(pageParam.getCurrent(), pageParam.getSize(), pageResult.getTotalElements());
        page.setRecords(pageResult.getContent());
        return page;
    }

    @Override
    public void update(LogKw logMsg, Long timestamp) {
        setSessionApiConfig(sessionApiConfig);
        BigdataBaseParam baseParam = BigdataBaseParam.builder().category(DataCategoryEnum.ES_KW_LOG).equipmentId(logMsg.getEquipmentId()).build();
        insert(baseParam, logMsg, timestamp);
    }

    @Override
    public void delete(List<Date> createTime) {
        BigdataBaseParam baseParam = BigdataBaseParam.builder().category(DataCategoryEnum.ES_KW_LOG).build();
        logKwIotDbMapper.delete(baseParam, createTime);
    }

    @Override
    public void insertBatch(List<LogKw> castDataList) {
        if (CollUtil.isEmpty(castDataList)) {
            return;
        }
        List<BigdataBaseParam> baseParamList = CollUtil.newArrayList();
        for (LogKw logMsg : castDataList) {
            BigdataBaseParam baseParam = BigdataBaseParam.builder().category(DataCategoryEnum.ES_KW_LOG).equipmentId(logMsg.getEquipmentId()).build();
            baseParamList.add(baseParam);
        }
        setSessionApiConfig(sessionApiConfig);
        insertBatch(baseParamList, castDataList);
    }

}
