package com.fatebug.base.cloud.anno;

import com.fatebug.base.cloud.feignConfig.FeignAutoConfiguration;
import com.fatebug.base.core.constants.LaunchConstants;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自定义feign注解
 * 添加basePackages路径
 *
 * @author fatebug
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
@Import(FeignAutoConfiguration.class)
public @interface EnableFateFeignClients
{
    String[] value() default {};

    String[] basePackages() default { LaunchConstants.API_PACKAGE ,LaunchConstants.LOG_PACKAGE };

    Class<?>[] basePackageClasses() default {};

    Class<?>[] defaultConfiguration() default {};

    Class<?>[] clients() default {};
}
