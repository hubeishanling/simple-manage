package org.shanling.simplemanage.vo;

import lombok.Data;
import java.util.Date;

/**
 * 脚本启动前预检查返回对象
 * 
 * @author shanling
 */
@Data
public class PreCheckVO {

    /**
     * Token是否有效
     */
    private Boolean tokenValid;

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
     * 剩余小时数
     */
    private Long remainingHours;

    /**
     * 剩余分钟数
     */
    private Long remainingMinutes;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 游戏名称
     */
    private String gameTitle;

    /**
     * 脚本版本号
     */
    private Integer version;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 提示消息
     */
    private String message;

}
