package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 脚本RSA密钥数据传输对象
 * 
 * @author shanling
 */
@Data
public class ScriptRsaKeyDTO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 密钥名称
     */
    @NotBlank(message = "密钥名称不能为空")
    private String keyName;

    /**
     * 请求加密公钥（前端使用）
     */
    @NotBlank(message = "请求加密公钥不能为空")
    private String requestPublicKey;

    /**
     * 请求解密私钥（后端使用）
     */
    @NotBlank(message = "请求解密私钥不能为空")
    private String requestPrivateKey;

    /**
     * 响应加密公钥（后端使用）
     */
    @NotBlank(message = "响应加密公钥不能为空")
    private String responsePublicKey;

    /**
     * 响应解密私钥（前端使用）
     */
    @NotBlank(message = "响应解密私钥不能为空")
    private String responsePrivateKey;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 是否为默认密钥（0否 1是）
     */
    private Integer isDefault;

    /**
     * 备注
     */
    private String remark;

}
