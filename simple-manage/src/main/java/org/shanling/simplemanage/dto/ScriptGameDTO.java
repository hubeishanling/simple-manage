package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 游戏列表数据传输对象
 * 
 * @author shanling
 */
@Data
public class ScriptGameDTO {

    /**
     * 游戏ID
     */
    private String id;

    /**
     * 游戏名称
     */
    @NotBlank(message = "游戏名称不能为空")
    private String title;

    /**
     * 备注
     */
    private String remark;

}
