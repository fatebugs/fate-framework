package com.fatebug.base.log.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 错误操作日志表
 *
 * @TableName fate_log_error
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "fate_log_error")
@Data
public class LogError extends LogAbstract implements Serializable {

    /**
     * 异常堆栈
     */
    private String stackTrace;

    /**
     * 异常名称
     */
    private String exceptionName;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 行号
     */
    private Integer lineNumber;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -5658928586813222134L;
}