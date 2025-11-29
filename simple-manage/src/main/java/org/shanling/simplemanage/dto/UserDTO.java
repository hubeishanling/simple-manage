package org.shanling.simplemanage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户数据传输对象
 * 
 * @author shanling
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3-20个字符")
    private String username;

    /**
     * 密码（新增时必填）
     */
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

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
