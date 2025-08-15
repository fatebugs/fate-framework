package com.fatebug.base.log.event;

import com.fatebug.base.launch.props.FateProperties;
import com.fatebug.base.log.constants.EventConstant;
import com.fatebug.base.log.domain.LogError;
import com.fatebug.base.log.service.ILogClient;
import com.fatebug.base.log.utils.LogAbstractUtil;
import com.fatebug.base.log.utils.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;


/**
 * 异步监听日志事件
 *
 * @author Chill
 */
@Slf4j
@AllArgsConstructor
public class LogErrorListener {

	private final ILogClient logService;
	private final ServerInfo serverInfo;
	private final FateProperties fateProperties;

	@Async
	@Order
	@EventListener(LogErrorEvent.class)
	public void saveApiLog(LogErrorEvent event) {
		Map<String, Object> source = (Map<String, Object>) event.getSource();
		LogError logError = (LogError) source.get(EventConstant.EVENT_LOG);
		LogAbstractUtil.addOtherInfoToLog(logError, fateProperties, serverInfo);
		logService.saveErrorLog(logError);
	}

}
