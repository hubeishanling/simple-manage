package org.shanling.simplemanage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.mapper.DashboardMapper;
import org.shanling.simplemanage.service.DashboardService;
import org.shanling.simplemanage.vo.DashboardStatsVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 首页统计Service业务层处理
 *
 * @author shanling
 */
@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    /**
     * 获取首页统计数据
     */
    @Override
    public DashboardStatsVO getDashboardStats() {
        log.info("开始获取首页统计数据");
        
        DashboardStatsVO stats = new DashboardStatsVO();

        try {
            // 统计收益
            BigDecimal totalRevenue = dashboardMapper.selectTotalRevenue();
            stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

            BigDecimal todayRevenue = dashboardMapper.selectTodayRevenue();
            stats.setTodayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO);

            // 统计卡密数量
            stats.setTotalCards(dashboardMapper.selectTotalCards());
            stats.setTodayCards(dashboardMapper.selectTodayCards());

            // 统计设备数量
            stats.setTotalDevices(dashboardMapper.selectTotalDevices());
            stats.setTodayDevices(dashboardMapper.selectTodayDevices());

            // 近7天卡密新增趋势
            stats.setCardTrend(dashboardMapper.selectCardTrend(7));

            // 近7天设备绑定趋势
            stats.setDeviceTrend(dashboardMapper.selectDeviceTrend(7));

            // 每个游戏下的卡密统计
            stats.setGameStats(dashboardMapper.selectGameCardStats());

            // 卡密状态统计
            stats.setCardStatusStats(dashboardMapper.selectCardStatusStats());

            // 近30天收益趋势
            stats.setRevenueTrend(dashboardMapper.selectRevenueTrend(30));

            log.info("首页统计数据获取成功");
        } catch (Exception e) {
            log.error("获取首页统计数据失败", e);
            throw new RuntimeException("获取首页统计数据失败: " + e.getMessage());
        }

        return stats;
    }
}
