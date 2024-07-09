package com.fcwenergy.iotdb.core.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.constant.BigDataConstant;
import lombok.NonNull;

/**
 * 约定大于配置，这里约束云平台设备在iotdb的路径规则
 * root.`category`.`operator`.`station`.`device`
 * category: 数据表类型，如日志流水log;指标记录record 这样配置，便于设置自动删除时间如 set ttl to root.log 3600000
 * operator: 运营商id
 * station: 站点id
 * device: 设备id
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
public class CesIotDbUtil {

    private static final String ROOT = "root";
    private static final String CHAR_SEPARATOR = "k";
    private static final String PATH_SEPARATOR = ".";
    private static final String PATH_ALL = "*";

    /**
     * 获取root path
     *
     * @param param   .
     * @param isQuery .
     * @return .
     */
    public static String getRootPath(BigdataBaseParam param, boolean isQuery) {
        if (ObjectUtil.isNull(param.getCategory())) {
            throw new IllegalArgumentException("category can not be null");
        }
        String op = convertDefaultPathParam(isQuery, param.getOperatorId(), BigDataConstant.ROOT_PATH_OPERATOR);
        String st = convertDefaultPathParam(isQuery, param.getStationId(), BigDataConstant.ROOT_PATH_STATION);
        String eq = convertDefaultPathParam(isQuery, param.getEquipmentId(), BigDataConstant.ROOT_PATH_DEVICE);
        String cn = convertDefaultPathParam(isQuery, param.getConnector(), BigDataConstant.ROOT_PATH_CONNECTOR);
        String lastPath = StrUtil.isNotBlank(param.getConnector()) ? cn : eq;
        return CesIotDbUtil.getFuzzyPath(param.getCategory().getCode(), op, st, lastPath);
    }

    private static String convertDefaultPathParam(boolean isQuery, Long param, String defaultPath) {
        if (isQuery) {
            return ObjectUtil.isNull(param) ? PATH_ALL : CHAR_SEPARATOR + param;
        }
        return ObjectUtil.isNull(param) ? defaultPath : CHAR_SEPARATOR + param;
    }

    private static String convertDefaultPathParam(boolean isQuery, String param, String defaultPath) {
        if (isQuery) {
            return ObjectUtil.isNull(param) ? PATH_ALL : CHAR_SEPARATOR + param;
        }
        return ObjectUtil.isNull(param) ? defaultPath : CHAR_SEPARATOR + param;
    }

    public static String getPointPath(@NonNull String category, Long operator, Long station, Long device) {
        String operatorPath = convertDefaultPartPath(operator);
        String stationPath = convertDefaultPartPath(station);
        String devicePath = convertDefaultPartPath(device);
        return getFuzzyPath(category, operatorPath, stationPath, devicePath);
    }

    public static String getMeasurementsPointPath(@NonNull String category, Long operator, Long station, Long device) {
        return getPointPath(category, operator, station, device) + PATH_SEPARATOR + PATH_ALL;
    }

    public static String getFuzzyPath(@NonNull String category, String operator, String station, String device) {
        String operatorPath = convertDefaultPartPath(operator);
        String stationPath = convertDefaultPartPath(station);
        String devicePath = convertDefaultPartPath(device);
        return ROOT + PATH_SEPARATOR + category + PATH_SEPARATOR + operatorPath + PATH_SEPARATOR + stationPath + PATH_SEPARATOR + devicePath;
    }

    public static String getMeasurementsFuzzyPath(@NonNull String category, String operator, String station, String device) {
        return getFuzzyPath(category, operator, station, device) + PATH_SEPARATOR + PATH_ALL;
    }

    private static String convertDefaultPartPath(Object param) {
        return ObjectUtil.isEmpty(param) ? PATH_ALL : String.valueOf(param);
    }

}
