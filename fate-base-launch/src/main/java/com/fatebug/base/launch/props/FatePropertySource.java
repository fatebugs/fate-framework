package com.fatebug.base.launch.props;

import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * 自定义资源文件读取，优先级最低
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FatePropertySource {

	/**
	 * 指示要加载的属性文件的资源位置。例如，
	 * {@code "classpath:/com/example/app.yml"}
	 *
	 * @return location(s)
	 */
	String[] values();

	/**
	 * load app-{activeProfile}.yml
	 *
	 * @return {boolean}
	 */
	boolean loadActiveProfile() default true;

	/**
	 * Get the order value of this resource.
	 *
	 * @return order
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
