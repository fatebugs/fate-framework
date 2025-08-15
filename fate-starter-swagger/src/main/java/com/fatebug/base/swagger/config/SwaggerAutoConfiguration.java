package com.fatebug.base.swagger.config;


import cn.hutool.core.collection.CollectionUtil;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.swagger.annotation.EnableSwagger;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * swagger配置
 */
@Slf4j
@EnableSwagger
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(SpringDocConfiguration.class)
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

    private static final String DEFAULT_BASE_PATH = "/**";
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");


    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI springDocOpenAPI() {
        OpenAPI openAPI = new OpenAPI();
        // 接口全局添加 Authorization 参数
        openAPI.info(new Info()
                             .title(swaggerProperties.getTitle())
                             .description(swaggerProperties.getDescription())
                             .version(swaggerProperties.getVersion())
                             .contact(swaggerProperties.getContact())
                             .license(new License()
                                              .name(swaggerProperties.getLicense())
                                              .url(swaggerProperties.getContact().getUrl()))
        );
        // Authorization-Token
        openAPI.addSecurityItem(
                new SecurityRequirement()
                        .addList(SysConstants.REFRESH_TOKEN)
                        .addList(SysConstants.ACCESS_TOKEN)
                        .addList(SysConstants.TENANT_HEADER)
        );
        openAPI.components(
                new Components()
                        .addSecuritySchemes(
                                SysConstants.ACCESS_TOKEN,
                                new SecurityScheme()
                                        .name(SysConstants.ACCESS_TOKEN)
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                                        .bearerFormat("JWT")
                                        .description(SysConstants.ACCESS_TOKEN)
                        ).addSecuritySchemes(
                                SysConstants.REFRESH_TOKEN,
                                new SecurityScheme()
                                        .name(SysConstants.REFRESH_TOKEN)
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                                        .description(SysConstants.REFRESH_TOKEN)
                        )
                        .addSecuritySchemes(
                                SysConstants.TENANT_HEADER,
                                new SecurityScheme()
                                        .name(SysConstants.TENANT_HEADER)
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                                        .description(SysConstants.TENANT_HEADER)
                        )
        )
        ;

        return openAPI;
    }


    /**
     * 初始化GlobalOpenApiCustomizer对象
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach(
                        (s, pathItem) ->
                                pathItem
                                        .readOperations().forEach(
                                                operation ->
                                                        operation.addSecurityItem(
                                                                new SecurityRequirement()
                                                                        .addList(SysConstants.ACCESS_TOKEN)
                                                                        .addList(SysConstants.REFRESH_TOKEN)
                                                                        .addList(SysConstants.TENANT_HEADER)
                                                        )
                                        )
                );
            }
            if (openApi.getTags() != null) {
                openApi.getTags().forEach(tag -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("x-order", tag.getDescription());
                    tag.setExtensions(map);
                });
            }
        };
    }

    /**
     * 初始化GroupedOpenApi对象
     */
    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi defaultApi() {
        // 如果Swagger配置中的基本路径和排除路径为空，则设置默认的基本路径和排除路径
        if (CollectionUtil.isEmpty(swaggerProperties.getBasePath())) {
            swaggerProperties.getBasePath().add(DEFAULT_BASE_PATH);
        }
        if (CollectionUtil.isEmpty(swaggerProperties.getExcludePath())) {
            swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }
        // 获取Swagger配置中的基本路径、排除路径、基本包路径和排除包路径
        List<String> basePath = swaggerProperties.getBasePath();
        List<String> excludePath = swaggerProperties.getExcludePath();
        List<String> basePackages = swaggerProperties.getBasePackages();
        List<String> excludePackages = swaggerProperties.getExcludePath();
        // 创建并返回GroupedOpenApi对象
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch(basePath.toArray(new String[0]))
                .pathsToExclude(excludePath.toArray(new String[0]))
                .packagesToScan(basePackages.toArray(new String[0]))
                .packagesToExclude(excludePackages.toArray(new String[0]))
                .build();
    }


}
