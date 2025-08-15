package com.fatebug.base.auth.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户登录信息存储类
 * @author fatebug
 */
@Data
public class TokenInfo<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7597435941765348486L;

    /**
     * 用户登录后的AccessToken
     */
    private String accessToken;

    /**
     * 用户登录后的RefreshToken
     */
    private String refreshToken;

    /**
     * 登录用户信息
     */
    private T user;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    /**
     * 登录IP地址
     */
    private String loginIp ="127.0.0.1";

    /**
     * 角色列表
     */
    @JsonIgnore
    private List<String> roles=new ArrayList<>();

    /**
     * 权限列表
     */
    @JsonIgnore
    private List<String> permissions=new ArrayList<>();

    public TokenInfo() {
    }

    public TokenInfo(T user) {
        this.user = user;
    }

    public TokenInfo<T> addRole(String role) {
        roles.add(role);
        return this;
    }

    public TokenInfo<T> addPermission(String permission) {
        permissions.add(permission);
        return this;
    }
}
