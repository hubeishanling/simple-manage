package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 脚本版本控制数据传输对象
 * 
 * @author shanling
 */
@Data
public class ScriptVersionControlDTO {

    /**
     * 版本ID
     */
    private String id;

    /**
     * 游戏ID
     */
    @NotBlank(message = "游戏ID不能为空")
    private String gameId;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件类型(0:APK插件)
     */
    @NotNull(message = "文件类型不能为空")
    private Integer type;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 备注
     */
    private String remark;

}
