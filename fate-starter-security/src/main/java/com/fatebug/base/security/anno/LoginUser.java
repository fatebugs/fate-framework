package com.fatebug.base.security.anno;

import java.lang.annotation.*;

/**
 * 请求的方法参数LoginUser上添加该注解，则注入当前登录人Token实体类信息
 *
 * @author fatebug
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {

}
