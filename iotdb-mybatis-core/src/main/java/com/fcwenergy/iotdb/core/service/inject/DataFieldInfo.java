package com.fcwenergy.iotdb.core.service.inject;

import lombok.Builder;
import lombok.Data;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

import java.util.List;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Data
@Builder
public class DataFieldInfo {
    private List<String> fields;

    private List<Object> values;

    private List<TSDataType> types;

    private Long timeStamp;
}
