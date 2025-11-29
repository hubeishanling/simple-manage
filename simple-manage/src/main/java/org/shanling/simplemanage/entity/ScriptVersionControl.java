package org.shanling.simplemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 脚本版本控制实体类
 * 
 * @author shanling
 */
@Data
@TableName("script_version_control")
public class ScriptVersionControl {

    /**
     * 版本ID（主键，雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件类型(0:APK插件)
     */
    private Integer type;

    /**
     * 版本(每次上传代码版本自动+1)
     */
    private Integer version;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

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
