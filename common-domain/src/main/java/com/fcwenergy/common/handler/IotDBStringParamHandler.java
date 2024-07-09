package com.fcwenergy.common.handler;

import cn.hutool.core.util.StrUtil;

public class IotDBStringParamHandler implements GetterParamHandler<String> {

    @Override
    public String getParam(String param) {
        if (StrUtil.isEmpty(param)) {
            return null;
        }
        return "'" + param + "'";
    }
}
