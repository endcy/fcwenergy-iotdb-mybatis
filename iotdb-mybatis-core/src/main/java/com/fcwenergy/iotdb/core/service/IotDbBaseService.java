package com.fcwenergy.iotdb.core.service;

import com.fcwenergy.common.domain.base.PageInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;
import java.util.List;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
public interface IotDbBaseService<T, Q> {

    /**
     * 分页条件查找
     * 没有不分页的查询，即使查询list也必须用分页，必须控制查询量，否则极易OOM
     *
     * @param query    .
     * @param pageable .
     * @return .
     */
    PageInfo<T> queryAll(Q query, Pageable pageable);

    /**
     * 新增
     *
     * @param logMsg    .
     * @param timestamp .
     */
    @Async("dbTaskExecutor")
    void insert(T logMsg, Long timestamp);

    /**
     * 修改
     *
     * @param logMsg .
     */
    @Async("dbTaskExecutor")
    void update(T logMsg, Long timestamp);

    /**
     * 二选一删除
     * 根据过期时间(包含)或者id列表
     *
     * @param createTime .
     */
    @Async("dbTaskExecutor")
    void delete(List<Date> createTime);

    /**
     * 批量插入数据
     *
     * @param castDataList .
     */
    @Async("dbTaskExecutor")
    void insertBatch(List<T> castDataList);
}
