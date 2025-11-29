package org.shanling.simplemanage.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日卡密趋势 视图对象
 *
 * @author shanling
 */
@Data
public class DailyCardTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 新增卡密数量
     */
    private Long count;
}
