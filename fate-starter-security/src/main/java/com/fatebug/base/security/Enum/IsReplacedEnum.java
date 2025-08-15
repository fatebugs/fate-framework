package com.fatebug.base.security.Enum;

import lombok.Getter;

/**
 * 基础是否枚举
 */
@Getter
public enum IsReplacedEnum {
    /**
     * 否
     */
    NO(0, "否"),
    /**
     * 是
     */
    YES(1, "是");

    private final Integer value;
    private final String name;

    IsReplacedEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

}
