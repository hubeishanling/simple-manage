package org.shanling.simplemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 脚本配置模板实体类
 * 
 * @author shanling
 */
@Data
@TableName("script_config")
public class ScriptConfig {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型（如：CONFIG_JS, VERSION_JSON等）
     */
    private String configType;

    /**
     * 配置内容（支持占位符）
     */
    private String configContent;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 是否为默认配置（0否 1是）
     */
    private Integer isDefault;

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
