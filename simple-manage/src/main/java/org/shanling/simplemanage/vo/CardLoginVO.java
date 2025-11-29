package org.shanling.simplemanage.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 卡密登录返回视图对象
 * 
 * @author shanling
 */
@Data
public class CardLoginVO {

    /**
     * 卡密token
     */
    private String cardToken;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 剩余天数
     */
    private Long remainingDays;

    /**
     * 可绑定设备数
     */
    private Integer deviceSize;

    /**
     * 关联的游戏列表
     */
    private List<GameInfo> games;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 游戏信息
     */
    @Data
    public static class GameInfo {
        private String gameId;
        private String gameTitle;
    }
}
