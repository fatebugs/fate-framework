package com.fatebug.base.core.anno;

import com.fatebug.base.core.config.ApplicationConfig;
import com.fatebug.base.core.constants.LaunchConstants;
import com.fatebug.base.launch.props.FateProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScans(@MapperScan(LaunchConstants.MAPPER_PACKAGE))
// 开启线程异步执行
@EnableAsync
// 自动扫描依赖包下的Bean
@ComponentScans(@ComponentScan({LaunchConstants.COMMON_PACKAGE, LaunchConstants.API_PACKAGE,LaunchConstants.BASE_PACKAGE}))
//开启事务
@EnableTransactionManagement
// 自动加载类
@ImportAutoConfiguration({ApplicationConfig.class})
// 开启缓存
@EnableCaching
public @interface EnableCustomConfig {

}
