package com.fatebug.base.log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 日志父类
 */
@Data
public class LogAbstract implements Serializable {
    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 服务id
     */
    private String serviceId;

    /**
     * 服务ip
     */
    private String serviceIp;

    /**
     * 服务主机
     */
    private String serviceHost;

    /**
     * 环境
     */
    private String env;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求地址
     */
    private String requestUri;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 请求ip
     */
    private String remoteIp;

    /**
     * 方法类
     */
    private String methodClass;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 4723697174229592561L;
}