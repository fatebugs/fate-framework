package com.fatebug.base.core.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;


/**
 * 树林节点
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ForestNode extends BaseNode<ForestNode> {

	@Serial
	private static final long serialVersionUID = -2190628415165417415L;

	/**
	 * 节点内容
	 */
	private Object content;

	public ForestNode(Long id, Long parentId, Object content) {
		this.id = id;
		this.parentId = parentId;
		this.content = content;
	}

}
