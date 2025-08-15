package com.fatebug.base.core.constants;

/**
 * 系统常量定义
 *
 * @author fatebug
 */
public interface SysConstants {

    /**
     * 系统管理员用户ID
     */
    int ADMIN_USER_ID = 0;

    /**
     * ture状态
     */
    int TURE = 1;

    /**
     * false状态
     */
    int FALSE = 0;

    /**
     * 删除状态[0:正常,1:删除]
     */
    int DB_NOT_DELETED = 0;
    int DB_IS_DELETED = 1;

    /**
     * 顶级父节点id
     */
    Long TOP_PARENT_ID = 0L;

    /**
     * 顶级父节点名称
     */
    String TOP_PARENT_NAME = "顶级";

    String TOP_ROLE_PARENT_NAME = "超级管理员";

    int SUPER_ADMIN_FLAG = 99;

    String SUPER_ADMIN = "SuperAdmin";

    String ALL_PERMISSION = "*:*:*";

    /**
     * 缓存有效期，默认720（分钟）
     */
    long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    long REFRESH_TIME = 120;


    /**
     * 字段间隔
     */
    String separation = "_";

    /**
     * 冒号常量
     */
    String COLON = ":";

    /**
     * 登录用户
     */
    String LOGIN_USER = "login_user";

    /**
     * access_token
     */
    String ACCESS_TOKEN = "access_token";

    /**
     * refresh_token
     */
    String REFRESH_TOKEN = "refresh_token";

    String TENANT_HEADER="Tenant-Id";

    /**
     * token对应的用户ip
     */
    String TOKEN_IP = "token_ip";
    /**
     * session_token_user_type
     */
    String USER_TYPE = "session_token_user_type";

    /*** 开关 -开 */
    String SWITCH_TRUE = "true";
    /*** 开关 -关 */
    String SWITCH_FALSE = "false";

    /**
     * 默认为空消息
     */
    String DEFAULT_NULL_MESSAGE = "暂无承载数据";
    /**
     * 默认成功消息
     */
    String DEFAULT_SUCCESS_MESSAGE = "操作成功";
    /**
     * 默认失败消息
     */
    String DEFAULT_FAILURE_MESSAGE = "操作失败";

    /**
     * token对应的用户id
     */
    String TOKEN_USERID = "token_userid";
    /**
     * token对应的用户名
     */
    String TOKEN_USERNAME = "token_username";
    /**
     * 角色权限
     */
    String ROLE_PERMISSION = "role_permission";
    /**
     * 请求来源
     */
    String FROM_SOURCE = "from-source";
    /**
     * 内部请求
     */
    String INNER = "inner";

    /**
     * 主键字段名
     */
    String DB_PRIMARY_KEY = "id";

    String DB_PRIMARY_KEY_CODE = "code";
    /**
     * 主键字段get方法
     */
    String DB_PRIMARY_KEY_METHOD = "getId";

    String DB_PRIMARY_KEY_CODE_METHOD = "getCode";

    String BASE_PACKAGES = "com.fatebug";

    /**
     * 租户字段名
     */
    String DB_TENANT_KEY = "tenantId";

    /**
     * 租户字段get方法
     */
    String DB_TENANT_KEY_GET_METHOD = "getTenantId";

    /**
     * 租户字段set方法
     */
    String DB_TENANT_KEY_SET_METHOD = "setTenantId";

    /**
     * gateway请求头
     */
    String GATEWAY_REQUEST_FLAG = "GATEWAY-REQUEST-FLAG";
    /**
     * gateway swagger请求头
     */
    String GATEWAY_SWAGGER_REQUEST_FLAG = "gateway-swagger-request-flag";
}
