package org.shanling.simplemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 脚本RSA密钥实体类
 * 
 * @author shanling
 */
@Data
@TableName("script_rsa_key")
public class ScriptRsaKey {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 密钥名称
     */
    private String keyName;

    /**
     * 请求加密公钥（前端使用）
     */
    private String requestPublicKey;

    /**
     * 请求解密私钥（后端使用）
     */
    private String requestPrivateKey;

    /**
     * 响应加密公钥（后端使用）
     */
    private String responsePublicKey;

    /**
     * 响应解密私钥（前端使用）
     */
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

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

}
