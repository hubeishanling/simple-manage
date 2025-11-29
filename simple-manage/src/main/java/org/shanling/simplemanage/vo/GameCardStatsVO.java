package org.shanling.simplemanage.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 游戏卡密统计 视图对象
 *
 * @author shanling
 */
@Data
public class GameCardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 卡密数量
     */
    private Long cardCount;

    /**
     * 激活数量（已绑定设备的卡密）
     */
    private Long activeCount;

    /**
     * 未激活数量
     */
    private Long inactiveCount;
}
