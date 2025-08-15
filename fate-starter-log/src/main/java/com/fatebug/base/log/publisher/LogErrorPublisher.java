package com.fatebug.base.log.publisher;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fatebug.base.utils.ServletUtils;
import com.fatebug.base.log.constants.EventConstant;
import com.fatebug.base.log.domain.LogError;
import com.fatebug.base.log.event.LogErrorEvent;
import com.fatebug.base.log.utils.LogAbstractUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * API日志信息事件发送
 *
 * @author Chill
 */
public class LogErrorPublisher {

	public static void publishEvent(Throwable error) {
		HttpServletRequest request = ServletUtils.getRequest();
		LogError logError = new LogError();
		if (ObjectUtil.isNotEmpty(error)) {
			logError.setStackTrace(ExceptionUtil.stacktraceToString(error));
			logError.setExceptionName(error.getClass().getName());
			logError.setMessage(error.getMessage());
			StackTraceElement[] elements = error.getStackTrace();
			if (ObjectUtil.isNotEmpty(elements)) {
				StackTraceElement element = elements[0];
				logError.setMethodName(element.getMethodName());
				logError.setMethodClass(element.getClassName());
				logError.setLineNumber(element.getLineNumber());
			}
		}
		LogAbstractUtil.addRequestInfoToLog(request, logError);

		Map<String, Object> event = new HashMap<>(16);
		event.put(EventConstant.EVENT_LOG, logError);
		SpringUtil.publishEvent(new LogErrorEvent(event));
	}

}
