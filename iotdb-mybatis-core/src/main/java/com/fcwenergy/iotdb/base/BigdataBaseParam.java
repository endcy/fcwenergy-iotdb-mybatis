package com.fcwenergy.iotdb.base;

import cn.hutool.core.util.StrUtil;
import com.fcwenergy.iotdb.core.util.CesIotDbUtil;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 大数据中必须包含的属性
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Data
@Builder
@Slf4j
public class BigdataBaseParam implements Serializable {
    private static final long serialVersionUID = -8292272661508794019L;

    private DataCategoryEnum category;

    private Long operatorId;

    private Long stationId;

    private Long equipmentId;

    private String connector;

    private Long timestamp;

    private String path;

    private boolean isQuery;

    private String sort = "desc";

    private int limit = 10000;

    private List<Date> createTime;

    @Tolerate
    public BigdataBaseParam() {
    }

    public String getPath() {
        if (StrUtil.isNotEmpty(this.path)) {
            return this.path;
        }
        this.path = CesIotDbUtil.getRootPath(this, isQuery);
        log.info(">>>>>>>>>> iotdb use path:{}", this.path);
        return this.path;
    }
}
