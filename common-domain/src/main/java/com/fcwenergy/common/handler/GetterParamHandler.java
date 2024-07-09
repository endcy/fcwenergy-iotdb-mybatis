package com.fcwenergy.common.handler;

/**
 * 查询参数加工执行器注解
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
public interface GetterParamHandler<T> {
    T getParam(T param);

}
