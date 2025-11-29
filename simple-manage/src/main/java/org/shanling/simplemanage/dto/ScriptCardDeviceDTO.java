package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 卡密关联设备数据传输对象
 * 
 * @author shanling
 */
@Data
public class ScriptCardDeviceDTO {

    /**
     * 设备ID
     */
    private String id;

    /**
     * 卡密
     */
    @NotBlank(message = "卡密不能为空")
    private String cardNo;

    /**
     * 设备的Android ID
     */
    @NotBlank(message = "设备Android ID不能为空")
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

}
