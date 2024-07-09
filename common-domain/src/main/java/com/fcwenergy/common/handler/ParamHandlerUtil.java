package com.fcwenergy.common.handler;

import cn.hutool.core.util.StrUtil;
import com.fcwenergy.common.annotation.QueryFieldTransformer;
import org.apache.ibatis.type.TypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册转换执行器
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
public class ParamHandlerUtil {
    private static final Map<String, GetterParamHandler<?>> REGISTERED_HANDLERS = new HashMap<>();

    /**
     * 使用反射获取示例，单机缓存
     */
    @SuppressWarnings("unchecked")
    public static <T> GetterParamHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        GetterParamHandler<T> retHandler;
        if (javaTypeClass != null) {
            String key = javaTypeClass.getSimpleName() + "-" + typeHandlerClass.getSimpleName();
            retHandler = (GetterParamHandler<T>) REGISTERED_HANDLERS.get(key);
            if (retHandler != null) {
                return retHandler;
            }
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                retHandler = (GetterParamHandler<T>) c.newInstance(javaTypeClass);
                REGISTERED_HANDLERS.put(key, retHandler);
                return retHandler;
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        String key = typeHandlerClass.getSimpleName();
        retHandler = (GetterParamHandler<T>) REGISTERED_HANDLERS.get(key);
        if (retHandler != null) {
            return retHandler;
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            retHandler = (GetterParamHandler<T>) c.newInstance();
            REGISTERED_HANDLERS.put(typeHandlerClass.getSimpleName(), retHandler);
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
        return retHandler;
    }

    public static <Q> Q getParam(Object entity, String fieldName, Q value) {
        if (value == null || (value instanceof String && StrUtil.isEmpty((String) value))) {
            return null;
        }
        try {
            //务必和属性一起更新
            Field field = entity.getClass().getDeclaredField(fieldName);
            Class<?> javaTypeClass = field.getDeclaringClass();
            QueryFieldTransformer fieldTransformer = field.getAnnotation(QueryFieldTransformer.class);
            if (fieldTransformer == null) {
                return value;
            }
            GetterParamHandler<Q> getterParamHandler = getInstance(javaTypeClass, fieldTransformer.typeHandler());
            return getterParamHandler.getParam(value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
