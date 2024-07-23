package com.fcwenergy.iotdb.dts.core;

import com.fcwenergy.iotdb.base.BigdataBaseParam;

import java.util.function.Function;

/**
 * ...
 *
 * @author cxx641
 * @date 2024/7/22 20:15:54
 */
public interface IBaseParamFunction<T> extends Function<T, BigdataBaseParam> {
    BigdataBaseParam apply(T t);
}
