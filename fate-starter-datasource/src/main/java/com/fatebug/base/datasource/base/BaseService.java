package com.fatebug.base.datasource.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 基础业务接口
 *
 * @param <T>
 */
public interface BaseService<T> extends IService<T> {

	/**
	 * 逻辑删除
	 *
	 * @param ids id集合
	 * @return
	 */
	boolean deleteLogic(@NotEmpty List<Long> ids);

	/**
	 * 变更状态
	 *
	 * @param ids    id集合
	 * @param status 状态值
	 * @return
	 */
	boolean changeStatus(@NotEmpty List<Long> ids, Integer status);

	boolean remove(Wrapper<T> queryWrapper);

}
