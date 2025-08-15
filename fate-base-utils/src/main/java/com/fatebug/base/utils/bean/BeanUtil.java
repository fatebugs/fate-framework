package com.fatebug.base.utils.bean;

import com.fatebug.base.utils.ClassUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * 实体工具类
 */
public class BeanUtil extends BeanUtils {

	/**
     * 将给定源bean的属性值复制到目标类中。
     * <p>
     * 注意：只要属性匹配，源类和目标类就不必匹配，甚至不必相互派生。
     * 源bean公开但目标bean没有公开的任何bean属性都将被默认忽略。
     * <p>
     * 这只是一种方便的方法。对于更复杂的传输需求，
	 *
	 * @param sourceList 源bean集合
	 * @param targetClazz 目标bean类
	 * @param <T>    泛型标记
	 * @return List
	 * @throws BeansException 如果复制失败
	 */
	public static <T> List<T> copyProperties(@Nullable Collection<?> sourceList, Class<T> targetClazz) throws BeansException {
		if (sourceList == null || sourceList.isEmpty()) {
			return Collections.emptyList();
		}
		List<T> outList = new ArrayList<>(sourceList.size());
		for (Object source : sourceList) {
			if (source == null) {
				continue;
			}
			T bean = BeanUtil.copyProperties(source, targetClazz);
			outList.add(bean);
		}
		return outList;
	}

	/**
     * 将给定源bean的属性值复制到目标类中。
     * <p>
     * 注意：只要属性匹配，源类和目标类就不必匹配，甚至不必相互派生。
     * 源bean公开但目标bean没有公开的任何bean属性都将被默认忽略。
     * <p>
     * 这只是一种方便的方法。对于更复杂的传输需求，
     *
	 * @param source 源bean
	 * @param targetClazz the target bean class
	 * @param <T>    泛型标记
	 * @return T
	 * @throws BeansException if the copying failed
	 */
	@Nullable
	public static <T> T copyProperties(@Nullable Object source, Class<T> targetClazz) throws BeansException {
		if (source == null) {
			return null;
		}
		T to = newInstance(targetClazz);
		BeanUtil.copyProperties(source, to);
		return to;
	}

	/**
	 * 实例化对象
	 *
	 * @param clazz 类
	 * @param <T>   泛型标记
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz) {
		return (T) instantiateClass(clazz);
	}

	/**
	 * 实例化对象
	 *
	 * @param clazzStr 类名
	 * @param <T>      泛型标记
	 * @return 对象
	 */
	public static <T> T newInstance(String clazzStr) {
		try {
			Class<?> clazz = ClassUtil.forName(clazzStr, null);
			return newInstance(clazz);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Bean的属性, 支持 propertyName 多级 ：test.user.name
	 *
	 * @param bean         bean
	 * @param propertyName 属性名
	 * @return 属性值
	 */
	@Nullable
	public static Object getProperty(@Nullable Object bean, String propertyName) {
		if (bean == null) {
			return null;
		}
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		return beanWrapper.getPropertyValue(propertyName);
	}

	/**
	 * 设置Bean属性, 支持 propertyName 多级 ：test.user.name
	 *
	 * @param bean         bean
	 * @param propertyName 属性名
	 * @param value        属性值
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		Objects.requireNonNull(bean, "bean Could not null");
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		beanWrapper.setPropertyValue(propertyName, value);
	}
}
