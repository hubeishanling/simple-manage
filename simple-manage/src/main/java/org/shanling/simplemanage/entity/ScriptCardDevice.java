package org.shanling.simplemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡密关联设备实体类
 * 
 * @author shanling
 */
@Data
@TableName("script_card_device")
public class ScriptCardDevice {

    /**
     * 设备ID（主键，雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 卡密
     */
    private String cardNo;

    /**
     * 设备的Android ID
     */
    private String deviceAndroidId;

    /**
     * 屏幕宽度
     */
    private Integer deviceWidth;

    /**
     * 屏幕高度
     */
    private Integer deviceHeight;

    /**
     * 厂商品牌
     */
    private String deviceBrand;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * API版本
     */
    private String deviceSdkInt;

    /**
     * 设备IMEI
     */
    private String deviceImei;

    /**
     * 主板型号
     */
    private String deviceBroad;

    /**
     * 版本号
     */
    private String deviceBuildId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

}
