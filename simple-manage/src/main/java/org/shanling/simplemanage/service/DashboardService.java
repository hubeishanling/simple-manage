package org.shanling.simplemanage.service;

import org.shanling.simplemanage.vo.DashboardStatsVO;

/**
 * 首页统计Service接口
 *
 * @author shanling
 */
public interface DashboardService {

    /**
     * 获取首页统计数据
     *
     * @return 首页统计数据
     */
    DashboardStatsVO getDashboardStats();
}
