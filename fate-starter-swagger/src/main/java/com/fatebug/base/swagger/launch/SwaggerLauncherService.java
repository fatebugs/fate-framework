package com.fatebug.base.swagger.launch;

import com.fatebug.base.auto.service.AutoService;
import com.fatebug.base.launch.constants.AppConstant;
import com.fatebug.base.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

import java.util.Properties;

/**
 * 初始化Swagger配置
 */
@AutoService(LauncherService.class)
public class SwaggerLauncherService implements LauncherService {
	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev) {
		Properties props = System.getProperties();
		props.setProperty("swagger.enabled", "true");
		props.setProperty("knife4j.enable", "true");
		props.setProperty("knife4j.production", "false");
		props.setProperty("spring.mvc.pathmatch.matching-strategy", "ANT_PATH_MATCHER");
		props.setProperty("springdoc.api-docs.enabled", "true");
		props.setProperty("springdoc.api-usage.enabled", "true");
		props.setProperty("springdoc.swagger-ui.enabled", "true");
		if (profile.equals(AppConstant.PROD_CODE)) {
			props.setProperty("swagger.enabled", "false");
			props.setProperty("knife4j.enable", "false");
			props.setProperty("knife4j.production", "true");
			props.setProperty("springdoc.api-docs.enabled", "false");
			props.setProperty("springdoc.api-usage.enabled", "false");
			props.setProperty("springdoc.swagger-ui.enabled", "false");
		}

	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
