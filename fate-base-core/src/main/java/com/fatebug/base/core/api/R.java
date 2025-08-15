package com.fatebug.base.core.api;

import com.fatebug.base.core.constants.SysConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回结果实体类
 *
 * @author fatebug
 */
@Data
@Accessors(chain = true)
public class R<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3533443302653850653L;

    private int code;
    private String msg;
    private T data;
    private boolean success = false;

    public R() {
        this(200, "", null);
    }

    public R(RespStatus status) {
        this(status.getCode(), status.getMsg(), null);
    }

    public R(RespStatus status, String msg) {
        this(status.getCode(), msg, null);
    }

    public R(RespStatus status, T data) {
        this(status.getCode(), status.getMsg(), data);
    }

    private R(RespStatus status, String msg, T data) {
        this(status.getCode(), msg, data);
    }

    /**
     * 返回值构造方法
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    private R(int code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = RespStatus.SUCCESS.getCode() == code;
    }


    /**
     * 返回R
     *
     * @param data 数据
     * @return R
     */
    public static <T> R<T> data(T data) {
        return data(SysConstants.DEFAULT_SUCCESS_MESSAGE, data);
    }

    /**
     * 返回R
     *
     * @param msg  消息
     * @param data 数据
     * @return R
     */
    private static <T> R<T> data(String msg, T data) {
        return data(HttpServletResponse.SC_OK, msg, data);
    }

    /**
     * 返回R
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @return R
     */
    private static <T> R<T> data(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    /**
     * 返回R
     *
     * @param msg 消息
     * @return R
     */
    public static <T> R<T> success(String msg) {
        return new R<>(RespStatus.SUCCESS, msg);
    }

    /**
     * 返回R
     *
     * @return R
     */
    public static <T> R<T> success() {
        return new R<>(RespStatus.SUCCESS);
    }


    /**
     * 返回R
     *
     * @param status 状态码枚举
     * @return R
     */
    public static <T> R<T> success(RespStatus status) {
        return new R<>(status);
    }

    /**
     * 返回R
     *
     * @param status 状态码枚举
     * @param msg    消息
     * @return R
     */
    public static <T> R<T> success(RespStatus status, String msg) {
        return new R<>(status, msg);
    }

    /**
     * 返回R
     *
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> error() {
        return new R<>(RespStatus.PARAM_ERROR);
    }

    /**
     * 返回R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> error(String msg) {
        return new R<>(RespStatus.PARAM_ERROR, msg);
    }

    /**
     * 返回R
     *
     * @param code 状态码
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> error(int code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 返回R
     *
     * @param status 状态码枚举
     * @return R
     */
    public static <T> R<T> error(RespStatus status) {
        return new R<>(status);
    }

    /**
     * 返回R
     *
     * @param status 状态码枚举
     * @param msg    消息
     * @return R
     */
    public static <T> R<T> error(RespStatus status, String msg) {
        return new R<>(status, msg);
    }

    /**
     * 返回R
     *
     * @param flag 成功状态
     * @return R
     */
    public static <T> R<T> status(boolean flag) {
        return flag ? success(SysConstants.DEFAULT_SUCCESS_MESSAGE) : error(SysConstants.DEFAULT_FAILURE_MESSAGE);
    }
}


