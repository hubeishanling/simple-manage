package org.shanling.simplemanage.config.encrypt;

import java.lang.annotation.*;

/**
 * 接口加密注解
 * 标注在需要加密的Controller方法上
 *
 * @author shanling
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEncrypt {

    /**
     * 响应加密，默认加密
     */
    boolean response() default true;

}
