package org.shanling.simplemanage.common;

/**
 * 统一状态码
 * 
 * @author shanling
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 4xx 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    // 业务错误码（1000-1999）
    BUSINESS_ERROR(1000, "业务处理失败"),
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "账号已被禁用"),
    USERNAME_EXISTS(1003, "用户名已存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    
    // 系统错误码（5000-5999）
    SYSTEM_ERROR(5000, "系统错误"),
    DATABASE_ERROR(5001, "数据库错误"),
    NETWORK_ERROR(5002, "网络错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
