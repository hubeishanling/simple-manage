package org.shanling.simplemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 脚本卡密实体类
 * 
 * @author shanling
 */
@Data
@TableName("script_card")
public class ScriptCard {

    /**
     * 卡密ID（主键，雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 卡密
     */
    private String cardNo;

    /**
     * 过期天数
     */
    private Integer expireDay;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 实际过期时间（初始为NULL，第一次绑定时赋值）
     */
    private Date expireTime;

    /**
     * 可绑定设备数
     */
    private Integer deviceSize;

    /**
     * 账号状态（0-停用，1-正常）
     */
    private Integer status;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

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

    /**
     * 备注
     */
    private String remark;

    /**
     * 关联的游戏列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<ScriptCardGame> cardGames;

}
