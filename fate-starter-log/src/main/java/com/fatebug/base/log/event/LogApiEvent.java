package com.fatebug.base.log.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 系统日志事件
 */
public class LogApiEvent extends ApplicationEvent {

	public LogApiEvent(Map<String, Object> source) {
		super(source);
	}

}
