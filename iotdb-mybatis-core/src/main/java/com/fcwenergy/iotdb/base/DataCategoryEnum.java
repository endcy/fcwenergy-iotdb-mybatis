package com.fcwenergy.iotdb.base;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataCategoryEnum {
    /**
     * aodc设备日志
     */
    AODC_LOG("aodcLog"),
    ESCHG_LOG("eschgLog"),
    MESE_LOG("meseLog"),
    METER_LOG("meterLog"),
    CEC102_LOG("cec102Log"),
    OCPP_1_6_LOG("ocpp16Log"),
    OCPP_1_6_METER_VALUE("ocpp16MeterValue"),
    METER_RECORD("meterRec"),
    EQUIPMENT_POWER("equipPower"),
    CURVE_INFO("curveInfo"),
    ES_KW_LOG("esKwLog"),
    ;

    @EnumValue
    @JsonValue
    private final String code;

    @JsonCreator
    public static DataCategoryEnum create(String value) {
        for (DataCategoryEnum gender : DataCategoryEnum.values()) {
            if (gender.code.equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }
}
