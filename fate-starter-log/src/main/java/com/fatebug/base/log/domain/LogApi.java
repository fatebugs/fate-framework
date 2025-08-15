package com.fatebug.base.log.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 操作日志表
 *
 * @TableName fate_log_api
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "fate_log_api")
@Data
public class LogApi extends LogAbstract implements Serializable {
    /**
     * 标题
     */
    private String title;

    /**
     * 事件时间
     */
    private String eventTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 7981418247126747638L;
}