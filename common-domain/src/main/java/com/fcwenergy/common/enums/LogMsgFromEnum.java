package com.fcwenergy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogMsgFromEnum {
    SERVER(1),
    CLIENT(2);

    @EnumValue
    @JsonValue    //标记响应json值
    private final Integer code;

    @JsonCreator
    public static LogMsgFromEnum create(Integer value) {
        for (LogMsgFromEnum gender : LogMsgFromEnum.values()) {
            if (gender.code.equals((value))) {
                return gender;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }
}
