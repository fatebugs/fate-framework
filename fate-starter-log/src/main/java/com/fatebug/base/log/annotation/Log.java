package com.fatebug.base.log.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author fatebug
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 日志描述
     *
     * @return {String}
     */
    String value() default "日志记录";
}
