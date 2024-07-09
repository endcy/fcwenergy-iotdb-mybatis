package com.fcwenergy.common.handler;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
public class DefaultTypeHandler implements GetterParamHandler<Object> {
    @Override
    public Object getParam(Object param) {
        return param;
    }
}
