package com.fatebug.base.core.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author fatebug
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RespStatus {

    /**
     * 请求成功
     */
    SUCCESS(HttpServletResponse.SC_OK, "成功"),

    /**
     * 服务器异常
     */
    ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器异常"),

    /**
     * 资源不存在
     */
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "资源不存在"),

    /**
     * 参数错误
     */
    PARAM_ERROR(HttpServletResponse.SC_BAD_REQUEST, "非法参数！"),

    /**
     * 拒绝访问
     */
    FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "拒绝访问！"),

    BODY_NOT_MATCH(600, "请求的数据格式不符!"),

    INTERNAL_SERVER_ERROR(700, "服务器内部错误!"),


    /**
     * 用户相关错误
     */
    NO_LOGIN(1001, "未登录或登陆失效！"),

    VEL_CODE_ERROR(1002, "验证码错误！"),

    USER_NOT_FOUND(1009,"用户不存在" ),

    USERNAME_EXIST(1003, "用户名已注册！"),

    USERNAME_PASS_ERROR(1004, "用户名或密码错误！"),

    TWO_PASSWORD_DIFF(1005, "两次输入的新密码不匹配!"),

    OLD_PASSWORD_ERROR(1006, "旧密码不匹配!"),

    TOKEN_FAIL(1008,"登录状态无效" ),

    TOKEN_NULL(1008,"未登录" ),

    TOKEN_INVALID(1008,"登录失效" ),

    USER_IS_BAN(1010,"用户封禁,你干了啥坏事呢" ),

    //没有可用的用户名
    NO_USERNAME(1011,"没有可用的用户名" ),

    //没有填写密码
    NO_PASSWORD(1012,"没有填写密码" ),

    //注册失败
    REGISTER_FAIL(1013,"注册失败" ),

    //请填写账号密码
    NO_USERNAME_PASSWORD(1014,"请填写账号密码" ),

    /**
     * 文件相关错误
     */
    FILE_DIR_MAKE_FAIL(10001, "目录创建失败"),

    FILE_SIZE_LIMIT(10003, "文件大小超出限制"),

    FILE_UPLOAD_FAIL(10004, "文件上传失败"),

    FILE_UPLOAD_OSS_FAIL(10004, "文件上传oss失败"),

    FILE_UPLOAD_SUCCESS(10005, "文件上传成功"),

    FILE_UPLOAD_OSS_SUCCESS(10005, "文件上传oss成功"),

    ;

    private int code;
    private String msg;


}
