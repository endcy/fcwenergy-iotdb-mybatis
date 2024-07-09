package com.fcwenergy.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 大数据属性注解
 * 由于第三方框架限制，该注解仅给插入更新数据使用，查询、删除还是使用mybatis
 * 如果注解值为空，则直接获取属性名称
 *
 * @author endcy
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataField {
    String value() default "";

    /**
     * @return 枚举TSDataType的值
     * @link org.apache.iotdb.tsfile.file.metadata.enums.TSDataType
     */
    int type() default 5;
}
