package com.fcwenergy.common.annotation;

import com.fcwenergy.common.handler.DefaultTypeHandler;
import com.fcwenergy.common.handler.GetterParamHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段加工执行器注解
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryFieldTransformer {
    /**
     * 字段加工执行器
     */
    Class<? extends GetterParamHandler> typeHandler() default DefaultTypeHandler.class;
}
