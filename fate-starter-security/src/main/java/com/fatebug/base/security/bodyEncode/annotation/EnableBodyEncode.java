package com.fatebug.base.security.bodyEncode.annotation;

import com.fatebug.base.security.bodyEncode.advice.EncryptRequestBodyAdvice;
import com.fatebug.base.security.bodyEncode.advice.EncryptResponseBodyAdvice;
import com.fatebug.base.security.bodyEncode.config.SecretKeyConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Author:Bobby
 * DateTime:2019/4/9 16:44
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({SecretKeyConfig.class,
        EncryptResponseBodyAdvice.class,
        EncryptRequestBodyAdvice.class})
public @interface EnableBodyEncode {

}
