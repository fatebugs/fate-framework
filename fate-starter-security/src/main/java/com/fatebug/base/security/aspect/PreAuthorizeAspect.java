package com.fatebug.base.security.aspect;

import com.fatebug.base.core.exception.PermissionException;
import com.fatebug.base.security.anno.HasPermission;
import com.fatebug.base.security.anno.HasRoles;
import com.fatebug.base.security.auth.AuthUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 基于 Spring Aop 的注解鉴权
 *
 * @author fatebug
 */
@Aspect
@Component
public class PreAuthorizeAspect {

    /**
     * 构建
     */
    public PreAuthorizeAspect() {
    }

    /**
     * 声明AOP签名
     */
    @Pointcut(
            "@annotation(com.fatebug.base.security.anno.HasRoles)||"+
            "@annotation(com.fatebug.base.security.anno.HasPermission)"
    )
    public void pointcut() {
    }

    /**
     * 环绕切入
     *
     * @param joinPoint 切面对象
     * @return 底层方法执行后的返回值
     * @throws Throwable 底层方法抛出的异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        checkMethodAnnotation(signature.getMethod());
        // 执行原有逻辑
        return joinPoint.proceed();
    }

    /**
     * 对一个Method对象进行注解检查
     */
    public void checkMethodAnnotation(Method method) {
        // 校验 @RequiresRoles 注解
        boolean roleFlag = false;
        HasRoles hasRoles = method.getAnnotation(HasRoles.class);
        if (hasRoles != null) {
            roleFlag = AuthUtil.checkRole(hasRoles);
        }

        // 校验 @RequiresPermissions 注解
        boolean permissionFlag = false;
        HasPermission hasPermission = method.getAnnotation(HasPermission.class);
        if (hasPermission != null) {
            permissionFlag = AuthUtil.checkPermi(hasPermission);
        }

        // 如果两个注解都存在，只需要判断其中一个即可
        if (hasRoles != null && hasPermission != null) {
            if (!roleFlag || !permissionFlag) {
                throw new PermissionException("权限不足");
            }
        } else {
            // 如果只有一个注解存在，单独判断
            if (hasRoles != null && !roleFlag) {
                throw new PermissionException("角色不足");
            }
            if (hasPermission != null && !permissionFlag) {
                throw new PermissionException("权限不足");
            }
        }
    }
}
