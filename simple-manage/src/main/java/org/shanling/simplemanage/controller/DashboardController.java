package org.shanling.simplemanage.controller;

import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.service.DashboardService;
import org.shanling.simplemanage.vo.DashboardStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页统计控制器
 *
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取首页统计数据
     * 包括收益、卡密、设备等各项统计数据
     */
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        try {
            DashboardStatsVO stats = dashboardService.getDashboardStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取首页统计数据失败", e);
            return Result.error("获取首页统计数据失败: " + e.getMessage());
        }
    }
}
