package com.fatebug.base.log.utils;


import cn.hutool.core.util.ObjectUtil;
import com.fatebug.base.auth.util.SecurityUtils;
import com.fatebug.base.core.constants.StringPool;
import com.fatebug.base.utils.ServletUtils;
import com.fatebug.base.launch.props.FateProperties;
import com.fatebug.base.utils.IpUtil;
import com.fatebug.base.log.domain.LogAbstract;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Log 相关工具
 *
 * @author Chill
 */
public class LogAbstractUtil {

	/**
	 * 向log中添加补齐request的信息
	 *
	 * @param request     请求
	 * @param logAbstract 日志基础类
	 */
	public static void addRequestInfoToLog(HttpServletRequest request, LogAbstract logAbstract) {
		if (ObjectUtil.isNotEmpty(request)) {
			logAbstract.setRemoteIp(IpUtil.getRealIP(request));
			logAbstract.setUserAgent(request.getHeader(ServletUtils.USER_AGENT_HEADER));
			logAbstract.setRequestUri(ServletUtils.getPath(request.getRequestURI()));
			logAbstract.setMethod(request.getMethod());
			logAbstract.setParams(ServletUtils.getRequestContent(request));
			logAbstract.setCreateBy(SecurityUtils.getUsername());
		}
	}

	/**
	 * 向log中添加补齐其他的信息（eg：server等）
	 *
	 * @param logAbstract     日志基础类
	 * @param fateProperties 配置信息
	 * @param serverInfo      服务信息
	 */
	public static void addOtherInfoToLog(LogAbstract logAbstract, FateProperties fateProperties, ServerInfo serverInfo) {
		logAbstract.setServiceId(fateProperties.getName());
		logAbstract.setServiceHost(serverInfo.getHostName());
		logAbstract.setServiceIp(serverInfo.getIpWithPort());
		logAbstract.setEnv(fateProperties.getEnv());
		if (logAbstract.getParams() == null) {
			logAbstract.setParams(StringPool.EMPTY);
		}else {
			logAbstract.setParams(logAbstract.getParams().length() > 2000 ? logAbstract.getParams().substring(0, 2000) : logAbstract.getParams());
		}
	}
}
