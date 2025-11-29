package org.shanling.simplemanage.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 首页统计数据 视图对象
 *
 * @author shanling
 */
@Data
public class DashboardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总收益
     */
    private BigDecimal totalRevenue;

    /**
     * 今日新增收益
     */
    private BigDecimal todayRevenue;

    /**
     * 总卡密数
     */
    private Long totalCards;

    /**
     * 今日新增卡密数
     */
    private Long todayCards;

    /**
     * 总设备数
     */
    private Long totalDevices;

    /**
     * 今日新增设备数
     */
    private Long todayDevices;

    /**
     * 近7天卡密新增趋势
     */
    private List<DailyCardTrendVO> cardTrend;

    /**
     * 近7天设备绑定趋势
     */
    private List<DailyDeviceTrendVO> deviceTrend;

    /**
     * 每个游戏下的卡密统计
     */
    private List<GameCardStatsVO> gameStats;

    /**
     * 卡密状态统计
     */
    private CardStatusStatsVO cardStatusStats;

    /**
     * 近30天收益趋势
     */
    private List<DailyRevenueTrendVO> revenueTrend;
}
