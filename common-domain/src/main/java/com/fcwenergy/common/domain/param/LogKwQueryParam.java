package com.fcwenergy.common.domain.param;

import com.fcwenergy.common.annotation.QueryFieldTransformer;
import com.fcwenergy.common.handler.IotDBStringParamHandler;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LogKwQueryParam extends BaseQueryParam {

    @Tolerate
    public LogKwQueryParam() {
    }

    /**
     * 精确
     */
    private Long equipmentId;

    /**
     * BETWEEN
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<Date> createTime;

    /**
     * 精确
     */
    @QueryFieldTransformer(typeHandler = IotDBStringParamHandler.class)
    private String name;
}
