package com.fcwenergy.common.domain.param;

import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
public class BaseQueryParam implements Serializable {

    private static final long serialVersionUID = -6929130029894803378L;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    /**
     * BETWEEN
     */
    @Getter
    private List<Date> createTime;

}
