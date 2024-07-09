package com.fcwenergy.iotdb.base;

import lombok.Data;

import java.io.Serializable;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Data
public class CountNumDTO implements Serializable {
    private static final long serialVersionUID = 5605016154962067851L;

    private Integer count;

    private String device;

}
