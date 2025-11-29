package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 脚本卡密数据传输对象
 * 
 * @author shanling
 */
@Data
public class ScriptCardDTO {

    /**
     * 卡密ID
     */
    private String id;

    /**
     * 卡密
     */
    @NotBlank(message = "卡密不能为空")
    private String cardNo;

    /**
     * 过期天数
     */
    @NotNull(message = "过期天数不能为空")
    private Integer expireDay;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 可绑定设备数
     */
    private Integer deviceSize;

    /**
     * 账号状态
     */
    @NotNull(message = "账号状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
