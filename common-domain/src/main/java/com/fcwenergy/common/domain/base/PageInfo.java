package com.fcwenergy.common.domain.base;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础分页数据类
 */
@Data
@Builder
@Accessors(chain = true)
public class PageInfo<T> implements Serializable {
    /**
     * 总数量
     */
    private long totalElements;

    /**
     * 内容
     */
    private List<T> content;

    public PageInfo(long totalElements, List<T> content) {
        this.totalElements = totalElements;
        this.content = content;
    }

    public PageInfo() {
    }

    public static <T> PageInfo<T> empty() {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setTotalElements(0);
        pageInfo.setContent(new ArrayList<>());
        return pageInfo;
    }
}
