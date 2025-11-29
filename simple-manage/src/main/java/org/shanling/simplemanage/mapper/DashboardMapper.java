package org.shanling.simplemanage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shanling.simplemanage.vo.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首页统计Mapper接口
 *
 * @author shanling
 */
@Mapper
public interface DashboardMapper {

    /**
     * 查询总收益
     */
    BigDecimal selectTotalRevenue();

    /**
     * 查询今日新增收益
     */
    BigDecimal selectTodayRevenue();

    /**
     * 查询总卡密数
     */
    Long selectTotalCards();

    /**
     * 查询今日新增卡密数
     */
    Long selectTodayCards();

    /**
     * 查询总设备数
     */
    Long selectTotalDevices();

    /**
     * 查询今日新增设备数
     */
    Long selectTodayDevices();

    /**
     * 查询近N天卡密新增趋势
     */
    List<DailyCardTrendVO> selectCardTrend(@Param("days") int days);

    /**
     * 查询近N天设备绑定趋势
     */
    List<DailyDeviceTrendVO> selectDeviceTrend(@Param("days") int days);

    /**
     * 查询每个游戏下的卡密统计
     */
    List<GameCardStatsVO> selectGameCardStats();

    /**
     * 查询卡密状态统计
     */
    CardStatusStatsVO selectCardStatusStats();

    /**
     * 查询近N天收益趋势
     */
    List<DailyRevenueTrendVO> selectRevenueTrend(@Param("days") int days);
}
