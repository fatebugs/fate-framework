package com.fatebug.base.security.auth;


import com.fatebug.base.auth.util.SecurityContextHolder;
import com.fatebug.base.auth.util.SecurityUtils;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.core.exception.AuthException;
import com.fatebug.base.utils.StringUtils;
import com.fatebug.base.security.Enum.Logical;
import com.fatebug.base.security.anno.HasPermission;
import com.fatebug.base.security.anno.HasRoles;
import com.fatebug.base.auth.user.TokenInfo;
import com.fatebug.base.auth.util.TokenUtil;
import org.springframework.util.PatternMatchUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Token 权限验证，逻辑实现类
 *
 * @author fatebug
 */
public class AuthLogic {

    /**
     * 会话注销
     */
    public void logout() {
        String token = SecurityUtils.getToken();
        if (token == null) {
            return;
        }
        logoutByToken(token);
    }

    /**
     * 会话注销，根据指定Token
     */
    public void logoutByToken(String token) {
        TokenUtil.delToken(token);
    }

    /**
     * 检验用户是否已经登录，如未登录，则抛出异常
     */
    public void checkLogin() {
        getTokenInfo();
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     *
     * @return 用户缓存信息
     */
    public <T> TokenInfo<T> getTokenInfo() {
        String token = SecurityUtils.getToken();
        if (token == null) {
            throw new AuthException("未提供token");
        }
        TokenInfo<T> tokenInfo = TokenUtil.getTokenInfo(token);
        if (tokenInfo == null) {
            throw new AuthException("无效的token");
        }
        return tokenInfo;
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     *
     * @param token 前端传递的认证信息
     * @return 用户缓存信息
     */
    public <T> TokenInfo<T> getTokenInfo(String token) {
        return TokenUtil.getTokenInfo(token);
    }

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermission(String permission) {
        return hasPermission(getPermiList(), permission);
    }

    /**
     * 验证用户是否具备某权限, 如果验证未通过，则抛出异常: NotPermissionException
     *
     * @param permission 权限字符串 throw 用户是否具备某权限
     */
    public void checkPerm(String permission) {
        if (!hasPermission(getPermiList(), permission)) {
            throw new AuthException("没有权限：" + permission);
        }
    }

    /**
     * 根据注解(@RequiresPermissions)鉴权, 如果验证未通过，则抛出异常: NotPermissionException
     *
     * @param hasPermission 注解对象
     */
    public boolean checkPerm(HasPermission hasPermission) {
        SecurityContextHolder.setPermission(StringUtils.join(hasPermission.value(), ","));
        if (hasPermission.logical() == Logical.AND) {
            return checkPermAnd(hasPermission.value());
        } else {
            return checkPermOr(hasPermission.value());
        }
    }

    /**
     * 验证用户是否含有指定权限，必须全部拥有
     *
     * @param permissions 权限列表
     */
    public boolean checkPermAnd(String... permissions) {
        List<String> permissionList = getPermiList();
        for (String permission : permissions) {
            if (!hasPermission(permissionList, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证用户是否含有指定权限，只需包含其中一个
     *
     * @param permissions 权限码数组
     */
    public boolean checkPermOr(String... permissions) {
        List<String> permissionList = getPermiList();
        for (String permission : permissions) {
            if (hasPermission(permissionList, permission)) {
                return true;
            }
        }
        return permissions.length <= 0;
    }

    /**
     * 判断用户是否拥有某个角色
     *
     * @param role 角色标识
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String role) {
        return hasRole(getRoleList(), role);
    }

    /**
     * 判断用户是否拥有某个角色, 如果验证未通过，则抛出异常: NotRoleException
     *
     * @param role 角色标识
     */
    public void checkRole(String role) {
        if (!hasRole(role)) {
            throw new AuthException("没有角色：" + role);
        }
    }

    /**
     * 根据注解(@RequiresRoles)鉴权
     *
     * @param hasRoles 注解对象
     */
    public boolean checkRole(HasRoles hasRoles) {
        if (hasRoles.logical() == Logical.AND) {
            return checkRoleAnd(hasRoles.value());
        } else {
            return checkRoleOr(hasRoles.value());
        }
    }

    /**
     * 验证用户是否含有指定角色，必须全部拥有
     *
     * @param roles 角色标识数组
     */
    public boolean checkRoleAnd(String... roles) {
        List<String> roleList = getRoleList();
        for (String role : roles) {
            if (!hasRole(roleList, role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证用户是否含有指定角色，只需包含其中一个
     *
     * @param roles 角色标识数组
     */
    public boolean checkRoleOr(String... roles) {
        List<String> roleList = getRoleList();
        for (String role : roles) {
            if (hasRole(roleList, role)) {
                return true;
            }
        }
        return roles.length == 0;
    }


    /**
     * 根据注解(@RequiresRoles)鉴权
     *
     * @param at 注解对象
     */
    public boolean checkByAnnotation(HasRoles at) {
        String[] roleArray = at.value();
        if (at.logical() == Logical.AND) {
            return this.checkRoleAnd(roleArray);
        } else {
            return this.checkRoleOr(roleArray);
        }
    }

    /**
     * 根据注解(@RequiresPermissions)鉴权
     *
     * @param at 注解对象
     */
    public boolean checkByAnnotation(HasPermission at) {
        String[] permissionArray = at.value();
        if (at.logical() == Logical.AND) {
            return this.checkPermAnd(permissionArray);
        } else {
            return this.checkPermOr(permissionArray);
        }
    }

    /**
     * 获取当前账号的角色列表
     *
     * @return 角色列表
     */
    public <T> List<String> getRoleList() {
        try {
            TokenInfo<T> tokenInfo = getTokenInfo();
            return tokenInfo.getRoles();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取当前账号的权限列表
     *
     * @return 权限列表
     */
    public <T> List<String> getPermiList() {
        try {
            TokenInfo<T> tokenInfo = getTokenInfo();
            return tokenInfo.getPermissions();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 判断是否包含权限
     *
     * @param authorities 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermission(Collection<String> authorities, String permission) {
        return authorities.stream()
                .filter(StringUtils :: hasText)
                .anyMatch(x -> SysConstants.ALL_PERMISSION.contains(x) || PatternMatchUtils.simpleMatch(x, permission));
    }

    /**
     * 判断是否包含角色
     *
     * @param roles 角色列表
     * @param role  角色
     * @return 用户是否具备某角色权限
     */
    public boolean hasRole(Collection<String> roles, String role) {
        return roles.stream().filter(StringUtils :: hasText).anyMatch(x -> SysConstants.SUPER_ADMIN.contains(x) || PatternMatchUtils.simpleMatch(x, role));
    }
}
