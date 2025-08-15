package com.fatebug.base.core.node;

import java.io.Serializable;
import java.util.List;

public interface INode<T> extends Serializable {

	/**
	 * 主键
	 *
	 * @return Long
	 */
	Long getId();

	/**
	 * 父主键
	 *
	 * @return Long
	 */
	Long getParentId();

	/**
	 * 子节点
	 *
	 * @return List<T>
	 */
	List<T> getChildren();

	/**
	 * 是否有子节点
	 *
	 * @return Boolean
	 */
	default Boolean getHasChildren() {
		return false;
	}

}
