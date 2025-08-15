package com.fatebug.base.datasource.param;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SuperQuery {
    /**
     * 查询类型
     * 1. input 文本
     * 2. date 日期
     */
    private String type;

    /**
     * 查询规则
     * 1. eq 等于
     * 2. like 模糊
     * 3. ne 不等于
     * 4. ni 不包含
     * 5. in 包含
     * 6. bt 区间
     */
    private String rule;

    /**
     * 查询字段
     */
    private String field;

    /**
     * 查询值
     */
    private String val;
}
