package org.shanling.simplemanage.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡密状态统计 视图对象
 *
 * @author shanling
 */
@Data
public class CardStatusStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 正常状态卡密数
     */
    private Long normalCount;

    /**
     * 停用状态卡密数
     */
    private Long disabledCount;

    /**
     * 已过期卡密数
     */
    private Long expiredCount;

    /**
     * 即将过期卡密数（7天内）
     */
    private Long expiringSoonCount;
}
