package com.fatebug.base.core.valid.newValid;

public interface PasswordPattern {
    String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,20}$";
    String PASSWORD_PATTERN_MESSAGE = "密码至少包含一个字母和一个数字且长度在6-20之间";

    String BASICS_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,20}$";
    String BASICS_PASSWORD_PATTERN_MESSAGE = "密码至少包含一个小写字母、一个大写字母和一个数字且长度在6-20之间";

    String COMPLEX_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$";
    String COMPLEX_PASSWORD_PATTERN_MESSAGE = "密码至少包含一个小写字母、一个大写字母、一个数字和一个特殊字符且长度在6-20之间";
}
