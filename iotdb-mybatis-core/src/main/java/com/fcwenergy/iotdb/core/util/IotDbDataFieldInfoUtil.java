package com.fcwenergy.iotdb.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fcwenergy.common.annotation.DataField;
import com.fcwenergy.common.annotation.IgnoreField;
import com.fcwenergy.iotdb.core.service.inject.DataFieldInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 获取iotdb属性、值、类型的工具类
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Slf4j
public class IotDbDataFieldInfoUtil {

    /**
     * 需要忽略的反射获取的字段
     */
    private static final List<String> IGNORE_FIELDS = CollUtil.newArrayList("serialVersionUID", "id");
    private static final List<String> TIMESTAMP_FIELDS = CollUtil.newArrayList("createTime", "createDate", "acquisitionTime");

    public static <T> DataFieldInfo generateFieldInfo(T param) {
        // 使用反射机制获取所有属性
        Field[] fields = param.getClass().getDeclaredFields();
        DataFieldInfo dataFieldInfo = resolveFieldAndValue(param, fields);
        Class<?> parentClass = param.getClass().getSuperclass();
        if (!parentClass.getSimpleName().equals(Object.class.getSimpleName())) {
            fields = parentClass.getDeclaredFields();
            DataFieldInfo subDataFieldInfo = resolveFieldAndValue(param, fields);
            dataFieldInfo.getFields().addAll(subDataFieldInfo.getFields());
            dataFieldInfo.getTypes().addAll(subDataFieldInfo.getTypes());
            dataFieldInfo.getValues().addAll(subDataFieldInfo.getValues());
            if (ObjectUtil.isNull(dataFieldInfo.getTimeStamp())) {
                dataFieldInfo.setTimeStamp(subDataFieldInfo.getTimeStamp());
            }
        }
        if (ObjectUtil.isNull(dataFieldInfo.getTimeStamp())) {
            dataFieldInfo.setTimeStamp(System.currentTimeMillis());
        }
        return dataFieldInfo;
    }

    private static <T> DataFieldInfo resolveFieldAndValue(T param, Field[] fields) {
        List<String> names = CollUtil.newArrayList();
        List<TSDataType> types = CollUtil.newArrayList();
        List<Object> values = CollUtil.newArrayList();
        DataFieldInfo dataFieldInfo = DataFieldInfo.builder().build();
        dataFieldInfo.setFields(names);
        dataFieldInfo.setTypes(types);
        dataFieldInfo.setValues(values);

        for (Field field : fields) {
            // 如果存在 @Field 注解，则读取注解值，并获取相应的属性值
            String fieldName = field.getName();
            Object fieldValue;
            TSDataType fieldType = TSDataType.TEXT;
            IgnoreField ignoreFieldAnnotation = field.getAnnotation(IgnoreField.class);
            if (ignoreFieldAnnotation != null && ignoreFieldAnnotation.value()) {
                continue;
            } else if (ignoreFieldAnnotation == null && IGNORE_FIELDS.contains(fieldName)) {
                continue;
            }
            // 读取注解值，并存放到 fields 数组中
            if (field.isAnnotationPresent(DataField.class)) {
                fieldName = field.getAnnotation(DataField.class).value();
                fieldType = TSDataType.getTsDataType((byte) field.getAnnotation(DataField.class).type());
            }
            try {
                // 通过反射机制获取属性值，并存放到 values 数组中
                field.setAccessible(true);
                fieldValue = field.get(param);
                // 获取枚举类型的mp指定属性值
                if (fieldValue instanceof Enum && !TSDataType.TEXT.equals(fieldType)) {
                    fieldValue = getEnumValue(fieldValue);
                }
                // 获取时间格式的值
                if (fieldValue instanceof Date) {
                    fieldValue = ((Date) fieldValue).getTime();
                }
                //BigDecimal处理
                if (fieldValue instanceof BigDecimal && TSDataType.DOUBLE.equals(fieldType)) {
                    fieldValue = ObjectUtil.isNull(fieldValue) ? null : ((BigDecimal) fieldValue).doubleValue();
                }
                if (fieldValue instanceof Long && (TIMESTAMP_FIELDS.contains(field.getName()) || TIMESTAMP_FIELDS.contains(fieldName))) {
                    dataFieldInfo.setTimeStamp((long) fieldValue);
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                log.warn("get field or value error:{}", e.getMessage());
                continue;
            }
            fieldName = StrUtil.emptyToDefault(fieldName, field.getName());
            //空值、空属性、空类型 不入库
            if (StrUtil.isEmpty(fieldName) || ObjectUtil.isNull(fieldValue) || ObjectUtil.isNull(fieldType)) {
                continue;
            }
            names.add(fieldName);
            values.add(fieldValue);
            types.add(fieldType);
        }
        return dataFieldInfo;
    }

    /**
     * 反射获取带有@EnumValue注解的属性值
     */
    private static Object getEnumValue(Object fieldValue) throws IllegalAccessException {
        if (fieldValue == null) {
            return null;
        }
        Field[] enumFields = fieldValue.getClass().getDeclaredFields();
        if (ArrayUtil.isEmpty(enumFields)) {
            return fieldValue.toString();
        }
        for (Field enumField : enumFields) {
            EnumValue targetField = enumField.getAnnotation(EnumValue.class);
            if (targetField != null) {
                enumField.setAccessible(true);
                return enumField.get(fieldValue);
            }
        }
        return fieldValue.toString();
    }

}
