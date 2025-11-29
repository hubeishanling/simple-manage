package org.shanling.simplemanage.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日设备绑定趋势 视图对象
 *
 * @author shanling
 */
@Data
public class DailyDeviceTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 新增设备数量
     */
    private Long count;
}
