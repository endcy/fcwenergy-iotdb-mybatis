package com.fcwenergy.common.domain.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fcwenergy.common.annotation.DataField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备系统功率
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Getter
@Setter
@NoArgsConstructor
public class LogKw {
    private static final long serialVersionUID = 1L;

    /**
     * 设备
     */
    @DataField(type = 2)
    private Long equipmentId;

    /**
     * 设备name
     */
    @DataField(type = 5)
    private String name;

    /**
     * 电网功率
     */
    @DataField(type = 4)
    private BigDecimal gridKw;

    /**
     * 光伏模组功率
     */
    @DataField(type = 4)
    private BigDecimal mpptKw;

    /**
     * 电池充放电功率
     */
    @DataField(type = 4)
    private BigDecimal battKw;

    /**
     * 电池充电功率
     */
    @DataField(type = 4)
    private BigDecimal battInKw;

    /**
     * 电池放电功率
     */
    @DataField(type = 4)
    private BigDecimal battOutKw;

    /**
     * 负载功率
     */
    @DataField(type = 4)
    private BigDecimal loadKw;

    /**
     * 创建时间
     */
    @DataField(type = 2)
    private Date createTime;

    public void copyFrom(LogKw source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
