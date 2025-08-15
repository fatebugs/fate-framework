package com.fatebug.base.datasource.base;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.utils.ClassUtil;
import com.fatebug.base.utils.bean.BeanUtil;
import com.fatebug.base.auth.user.TokenInfo;
import com.fatebug.base.auth.util.SecurityUtils;
import jakarta.validation.constraints.NotEmpty;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 业务封装基础类
 *
 * @param <M> mapper
 * @param <T> model
 */
@Validated
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

	@Override
	public boolean save(T entity) {
		this.resolveEntity(entity);
		return super.save(entity);
	}

	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		entityList.forEach(this::resolveEntity);
		return super.saveBatch(entityList, batchSize);
	}

	@Override
	public boolean updateById(T entity) {
		this.resolveEntity(entity);
		return super.updateById(entity);
	}

	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		entityList.forEach(this::resolveEntity);
		return super.updateBatchById(entityList, batchSize);
	}

	@Override
	public boolean saveOrUpdate(T entity) {
//		if (entity.getId() == null) {
//			return this.save(entity);
//		} else {
//			return this.updateById(entity);
//		}
		this.resolveEntity(entity);
		return super.saveOrUpdate(entity);
	}

	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		entityList.forEach(this::resolveEntity);
		return super.saveOrUpdateBatch(entityList, batchSize);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean remove(Wrapper<T> queryWrapper) {
		T entity = BeanUtil.newInstance(currentModelClass());
		TokenInfo<Object> user = SecurityUtils.getLoginUser();
		if (user != null) {
			entity.setUpdateUser(user.getUserId());
		}
		entity.setUpdateTime(new Date());
//		entity.setIsDeleted(1);
		// 开启mybatis-plus的启动填充语句后，update无法设置isdeleted=1
		// 所以通过update更新更新时间，remove更新删除标记
		return super.update(entity, queryWrapper) && super.remove(queryWrapper);
	}



	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteLogic(@NotEmpty List<Long> ids) {
		TokenInfo<Object> user = SecurityUtils.getLoginUser();
		List<T> list = new ArrayList<>();
		ids.forEach(id -> {
			T entity = BeanUtil.newInstance(getEntityClass());
			if (user != null) {
				entity.setUpdateUser(user.getUserId());
			}
			entity.setUpdateTime(new Date());
			entity.setId(id);
//			entity.setIsDeleted(1);
			list.add(entity);
		});
		// 开启mybatis-plus的启动填充语句后，update无法设置isdeleted=1
		// 所以通过update更新更新时间，remove更新删除标记
		return super.updateBatchById(list) && super.removeByIds(ids);
	}

	@Override
	public boolean changeStatus(@NotEmpty List<Long> ids, Integer status) {
		TokenInfo<Object> user = SecurityUtils.getLoginUser();
		List<T> list = new ArrayList<>();
		ids.forEach(id -> {
			T entity = BeanUtil.newInstance(currentModelClass());
			if (user != null) {
				entity.setUpdateUser(user.getUserId());
			}
			entity.setUpdateTime(new Date());
			entity.setId(id);
			entity.setStatus(status);
			list.add(entity);
		});
		return super.updateBatchById(list);
	}

	@SneakyThrows
	private void resolveEntity(T entity) {
		TokenInfo<Object> user = SecurityUtils.getLoginUser();
		Date now = new Date();
		if (entity.getId() == null) {
			// 处理新增逻辑
			if (user != null) {
				entity.setCreateUser(user.getUserId());
				entity.setUpdateUser(user.getUserId());
			}
			if (entity.getStatus() == null) {
				entity.setStatus(SysConstants.TURE);
			}
			entity.setCreateTime(now);
		} else if (user != null) {
			// 处理修改逻辑
			entity.setUpdateUser(user.getUserId());
		}
		// 处理通用逻辑
		entity.setUpdateTime(now);
		entity.setIsDel(SysConstants.DB_NOT_DELETED);
		// 处理多租户逻辑，若字段值为空，则不进行操作
		Field field = ReflectUtil.getField(entity.getClass(), SysConstants.DB_TENANT_KEY);
		if (ObjectUtil.isNotEmpty(field)) {
			Method getTenantId = ClassUtil.getMethod(entity.getClass(), SysConstants.DB_TENANT_KEY_GET_METHOD);
			String tenantId = String.valueOf(getTenantId.invoke(entity));
			if (ObjectUtil.isEmpty(tenantId)) {
				Method setTenantId = ClassUtil.getMethod(entity.getClass(), SysConstants.DB_TENANT_KEY_SET_METHOD, String.class);
				setTenantId.invoke(entity, (Object) null);
			}
		}
	}

}
