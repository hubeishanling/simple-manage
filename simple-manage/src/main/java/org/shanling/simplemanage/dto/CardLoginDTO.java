package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 卡密登录数据传输对象
 * 
 * @author shanling
 */
@Data
public class CardLoginDTO {

    /**
     * 卡号
     */
    @NotBlank(message = "卡号不能为空")
    private String cardNo;

    /**
     * 设备Android ID
     */
    @NotBlank(message = "设备Android ID不能为空")
    private String deviceAndroidId;

    /**
     * 设备屏幕宽度
     */
    private Integer deviceWidth;

    /**
     * 设备屏幕高度
     */
    private Integer deviceHeight;

    /**
     * 修订版本号
     */
    private String deviceBuildId;

    /**
     * 主板型号
     */
    private String deviceBroad;

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
     * 安卓系统API版本
     */
    private String deviceSdkInt;

    /**
     * 设备IMEI
     */
    private String deviceImei;

    /**
     * 设备信息（可选）
     */
    private String deviceInfo;

}
