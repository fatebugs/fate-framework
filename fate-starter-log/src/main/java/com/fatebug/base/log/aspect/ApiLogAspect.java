package com.fatebug.base.log.aspect;

import com.fatebug.base.log.annotation.Log;
import com.fatebug.base.log.publisher.LogApiPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
/**
 * 操作日志使用spring event异步入库
 */
@Slf4j
@Aspect
public class ApiLogAspect {

	@Around("@annotation(log)")
	public Object around(ProceedingJoinPoint point, Log log) throws Throwable {
		//获取类名
		String className = point.getTarget().getClass().getName();
		//获取方法
		String methodName = point.getSignature().getName();
		// 发送异步日志事件
		long beginTime = System.currentTimeMillis();
		//执行方法
		Object result = point.proceed();
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;
		//记录日志
		LogApiPublisher.publishEvent(methodName, className, log, time);
		return result;
	}

}
