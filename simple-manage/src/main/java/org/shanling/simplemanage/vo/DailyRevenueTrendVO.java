package org.shanling.simplemanage.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 每日收益趋势 视图对象
 *
 * @author shanling
 */
@Data
public class DailyRevenueTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 当日收益
     */
    private BigDecimal revenue;
}
