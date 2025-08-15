package com.fatebug.base.log.publisher;

import cn.hutool.extra.spring.SpringUtil;
import com.fatebug.base.utils.ServletUtils;
import com.fatebug.base.log.annotation.Log;
import com.fatebug.base.log.constants.EventConstant;
import com.fatebug.base.log.domain.LogApi;
import com.fatebug.base.log.event.LogApiEvent;
import com.fatebug.base.log.utils.LogAbstractUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * API日志信息事件发送
 *
 * @author Chill
 */
public class LogApiPublisher {

	public static void publishEvent(String methodName, String methodClass, Log log, Long time) {
		HttpServletRequest request = ServletUtils.getRequest();
		LogApi logApi = new LogApi();
		logApi.setTitle(log.value());
		logApi.setEventTime(String.valueOf(time));
		logApi.setMethodClass(methodClass);
		logApi.setMethodName(methodName);
		LogAbstractUtil.addRequestInfoToLog(request, logApi);
		Map<String, Object> event = new HashMap<>(16);
		event.put(EventConstant.EVENT_LOG, logApi);
		SpringUtil.publishEvent(new LogApiEvent(event));
	}

}
