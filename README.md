# fate-framework

## 简介

`fate-framework` 是一个基于 Java 和 Maven 构建的云服务基础框架，旨在为分布式系统开发提供统一的基础能力和扩展组件。该框架包含自动配置、核心功能、工具类、认证、数据源、日志、Redis、权限安全、Swagger 等多个模块，方便开发者按需集成。

## 特性

- 模块化设计，按需引入 starter 组件
- 支持 Spring Boot 3.x 与 Spring Cloud 2023.x
- 内置认证、权限、日志、数据源、Redis、Swagger 等常用能力
- 统一依赖管理，简化版本冲突
- 便捷的自动配置与扩展机制
- 适用于微服务与云原生场景

## 目录结构

```
fate-framework/
├── fate-base-auto/         # 自动配置模块
├── fate-base-cloud/        # 云基础模块
├── fate-base-core/         # 核心功能模块
├── fate-base-launch/       # 启动模块
├── fate-base-test/         # 测试模块
├── fate-base-utils/        # 工具类模块
├── fate-starter-auth/      # 认证组件
├── fate-starter-datasource/# 数据源组件
├── fate-starter-log/       # 日志组件
├── fate-starter-redis/     # Redis 组件
├── fate-starter-security/  # 权限安全组件
├── fate-starter-swagger/   # Swagger 文档组件
├── fate-zpom/              # 统一依赖管理
└── pom.xml                 # 父级 Maven 配置
```

## 快速开始

### 环境要求

- JDK 17 及以上
- Maven 3.6 及以上

### 构建项目

在项目根目录下执行：

```bash
mvn clean install
```


## 主要模块说明

- **fate-base-auto**：自动化配置相关代码，简化 starter 使用
- **fate-base-core**：核心基础功能，如通用异常、响应封装等
- **fate-base-utils**：常用工具类集合
- **fate-base-cloud**：云原生相关基础能力
- **fate-base-launch**：统一启动入口
- **fate-base-test**：测试相关工具与基类
- **fate-starter-auth**：认证与鉴权 starter
- **fate-starter-datasource**：多数据源与动态数据源支持
- **fate-starter-log**：日志采集与追踪 starter
- **fate-starter-redis**：Redis 集成 starter
- **fate-starter-security**：权限安全 starter
- **fate-starter-swagger**：Swagger/OpenAPI 文档集成
- **fate-zpom**：统一依赖版本管理

## 常见问题

1. **如何引入某个 starter？**  
   在你的业务模块的 `pom.xml` 中添加对应 starter 依赖即可。
2. **如何自定义配置？**  
   各 starter 支持通过 `application.yml`/`application.properties` 进行自定义配置，详见各模块文档或源码注释。
3. **兼容性如何？**  
   推荐使用 JDK 17+，Spring Boot 3.2.x，Spring Cloud 2023.x。



## 快速开始

SpringBoot业务模块引入`fate-base-auto`模块与`fate-base-launch`模块

```
<dependency>
    <groupId>com.fatebug.cloud</groupId>
    <artifactId>fate-base-launch</artifactId>
</dependency>
<dependency>
    <groupId>com.fatebug.cloud</groupId>
    <artifactId>fate-base-auto</artifactId>
    <scope>provided</scope>
</dependency>
```

修改启动类代码为：

```Java
import com.fatebug.base.core.anno.EnableCustomConfig;
import com.fatebug.common.constants.ServiceNameConstants;
import com.fatebug.base.launch.FateApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableCustomConfig//基础开关配置注解，默认包路径为com.fatebug
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FateGateway {
    public static void main(String[] args) {
//        SpringApplication.run(FateGateway.class, args);
        FateApplication.run(
                "gateway",//模块名
                FateGateway.class, args);
    }
}


```

建立`com.fatebug.base.launch.service.LauncherService`类的实现类，用于设置启动参数

**必须**添加注解`@AutoService(LauncherService.class)`用于自动配置扫描

示例：

```java
import com.fatebug.base.auto.service.AutoService;
import com.fatebug.common.constants.NacosConstant;
import com.fatebug.base.launch.service.LauncherService;
import com.fatebug.base.utils.Fate;
import com.fatebug.common.constants.ConfigConstants;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

/**
 * 启动参数拓展
 */
@AutoService(LauncherService.class)
public class BaseLauncherServiceImpl implements LauncherService {

    /**
     * 启动器
     *
     * @param builder    构建器
     * @param appName    应用程序名称
     * @param profile    配置文件
     * @param isLocalDev 是本地开发
     */
    @Override
    public void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev) {
        Properties props = System.getProperties();
        // 通用注册
        Fate.setProperty(props, "spring.cloud.nacos.discovery.server-addr", NacosConstant.nacosAddr(profile));
        Fate.setProperty(props, "spring.cloud.nacos.username", NacosConstant.NACOS_USERNAME);
        Fate.setProperty(props, "spring.cloud.nacos.password", NacosConstant.NACOS_PASSWORD);
        Fate.setProperty(props, "spring.cloud.nacos.config.server-addr", NacosConstant.nacosAddr(profile));

        Fate.setProperty(props,"spring.cloud.nacos.config.file-extension", NacosConstant.NACOS_CONFIG_FORMAT);
        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[0].data-id", NacosConstant.sharedDataId());
//        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[0].group", NacosConstant.NACOS_CONFIG_GROUP);
        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[0].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[1].data-id", NacosConstant.sharedDataId(profile));
//        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[1].group", NacosConstant.NACOS_CONFIG_GROUP);
        Fate.setProperty(props, "spring.cloud.nacos.config.shared-configs[1].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[2].data-id", NacosConstant.shareConfig(profile));
//        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[2].group", NacosConstant.NACOS_CONFIG_GROUP);
        Fate.setProperty(props,"spring.cloud.nacos.config.shared-configs[2].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
        Fate.setProperty(props,"spring.cloud.nacos.config.namespace", NacosConstant.NAME_SPACE);
        Fate.setProperty(props,"spring.cloud.nacos.discovery.namespace", NacosConstant.NAME_SPACE);
//        Fate.setProperty(props,"spring.cloud.nacos.discovery.namespace", NacosConstant.NAME_SPACE_ANIXUIL);

//		Fate.setProperty(props, "spring.cloud.sentinel.transport.dashboard", ConfigConstants.sentinelAddr(profile));
//		Fate.setProperty(props, "spring.zipkin.base-url", ConfigConstants.zipkinAddr(profile));
//		Fate.setProperty(props, "spring.datasource.dynamic.enabled", "false");

        // 开启elk日志
//		Fate.setProperty(props, "fate.log.elk.destination", ConfigConstants.elkAddr(profile));

        // seata注册地址
//		 Fate.setProperty(props, "seata.service.grouplist.default", ConfigConstants.seataAddr(profile));
        // seata注册group格式
//		 Fate.setProperty(props, "seata.tx-service-group", ConfigConstants.seataServiceGroup(appName));
        // seata配置服务group
//		 Fate.setProperty(props, "seata.service.vgroup-mapping.".concat(ConfigConstants.seataServiceGroup(appName)), ConfigConstants.DEFAULT_MODE);
        // seata注册模式配置
        //  Fate.setProperty(props, "seata.registry.type", ConfigConstants.NACOS_MODE);
        //  Fate.setProperty(props, "seata.registry.nacos.server-addr", ConfigConstants.nacosAddr(profile));
        //  Fate.setProperty(props, "seata.config.type", ConfigConstants.NACOS_MODE);
        //  Fate.setProperty(props, "seata.config.nacos.server-addr", ConfigConstants.nacosAddr(profile));
    }
}
```

Redis、数据库等配置也可以通过类似的方式进行注入



## 模块详情

- [fate-base-core](./fate-base-core/README.md) 基础代码模块
- [fate-base-utils](./fate-base-utils/README.md) 框架工具类模块
- [fate-base-auto](./fate-base-auto/README.md) 自动配置模块
- [fate-base-cloud](./fate-base-cloud/README.md) cloud基础配置与工具模块
- [fate-base-launch](./fate-base-launch/README.md) 启动模块
- [fate-base-test](./fate-base-test/README.md) 测试配置模块
- [fate-starter-auth](./fate-starter-auth/README.md) 认证基础模块
- [fate-starter-datasource](./fate-starter-datasource/README.md) 数据源模块
- [fate-starter-log](./fate-starter-log/README.md) 日志模块
- [fate-starter-redis](./fate-starter-redis/README.md) Redis 模块
- [fate-starter-security](./fate-starter-security/README.md) 权限安全模块
- [fate-starter-swagger](./fate-starter-swagger/README.md) Swagger 模块
- [fate-zpom](./fate-zpom/README.md) 统一依赖管理模块


## 贡献指南

1. Fork 本仓库
2. 新建分支进行开发
3. 提交 Pull Request
4. 遵循 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/v1.0.0/) 规范

## 联系方式

- 作者邮箱：1339524041@qq.com
- Issues：https://github.com/fatebugs/fate-framework/issues

## License

本项目采用 [Apache License 2.0](./LICENSE) 开源协议。

```
Copyright [2025] [fatebugs]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```