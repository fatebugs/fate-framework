package com.fatebug.base.datasource.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分页数据实体类
 *
 * @author fatebug
 */
@Data
public class PageData<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3533443302653850653L;

    /**
     * 每页大小
     */
    private long size=10;
    /**
     * 当前页
     */
    private long current=1;
    /*
     * 前端控制参数查询字段
     * 例子：文本：用户名叫'张三'的模糊查询
     * {
     *     "type":"input",
     *     "rule":"like",
     *     "field":"username",
     *     "val":"张三"
     * }
     */

    /**
     * 查询条件
     */
    private List<SuperQuery> params;

    /**
     * 实体条件
     */
    private T entity;

    /**
     * 正序排序字段
     */
    private List<String> ascSort;

    /**
     * 倒序排序字段
     */
    private List<String> descSort;

    /**
     * 条件字段
     * 前端传入的多参数，灵活运用
     */
    private Map<String, String> filter;

}
