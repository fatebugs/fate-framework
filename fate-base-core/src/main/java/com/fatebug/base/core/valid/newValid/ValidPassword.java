package com.fatebug.base.core.valid.newValid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default PasswordPattern.PASSWORD_PATTERN_MESSAGE;
    String pattern() default PasswordPattern.PASSWORD_PATTERN;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}