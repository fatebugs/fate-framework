package com.fatebug.base.security.Enum;

import lombok.Getter;

import java.io.Serializable;

/**
 * 用户类型枚举
 */
@Getter
public enum UserTypeEnum implements Serializable {

    F_USER_TYPE("GENERALUSER", "普通用户", "1", "com.fatebug.entity.user.LoginUser"),

    SYS_USER_TYPE("SYSUSER", "系统用户", "2", "com.fatebug.entity.user.SysUser"),


    ;

    private final String value;
    private final String name;
    private final String accountType;
    private final String className;

    UserTypeEnum(String value, String name, String accountType, String className) {
        this.value = value;
        this.name = name;
        this.accountType = accountType;
        this.className = className;
    }

    /**
     * 获取名称
     *
     * @param value 传入值
     * @return: java.lang.String 获取名称
     */
    public static String getName(String value) {
        for (UserTypeEnum bs : UserTypeEnum.values()) {
            if (bs.getValue().equals(value)) {
                return bs.getName();
            }
        }
        return null;
    }

    /**
     * 获取类型
     *
     * @param accountType 传入类型
     */
    public static UserTypeEnum getTokenTypeByAccountType(String accountType) {
        for (UserTypeEnum bs : UserTypeEnum.values()) {
            if (bs.getAccountType().equals(accountType)) {
                return bs;
            }
        }
        return null;
    }

    /**
     * 根据value 获取userType
     */
    public static UserTypeEnum getTokenTypeByValue(String value) {
        for (UserTypeEnum bs : UserTypeEnum.values()) {
            if (bs.getValue().equals(value)) {
                return bs;
            }
        }
        return null;
    }


    /**
     * 获取类型
     */
    public static UserTypeEnum getTokenTypeByUrl(String url) {
        String sysUser = "/sys/";
        if (url.contains(sysUser)) {
            return UserTypeEnum.SYS_USER_TYPE;
        }
        return UserTypeEnum.F_USER_TYPE;

    }

}
