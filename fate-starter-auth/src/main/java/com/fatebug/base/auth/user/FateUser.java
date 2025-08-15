package com.fatebug.base.auth.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fatebug.base.core.valid.groups.SearchGroups;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author fatebug
 */
@Data
@TableName("fate_user")
public class FateUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 8611377099332669696L;

    /**
     * 用户登录名（唯一）
     */
    private String username;

    /**
     * 用户密码
     */
    @JsonIgnore
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 管理员标记
     */
    private int admin=0;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;

    /**
     * 创建人
     */
    @Null(message = "查询时不可传入创建人", groups = SearchGroups.GroupA.class)
    private Long createUser;

    /**
     * 创建时间
     */
    @Null(message = "查询时不可传入创建时间", groups = SearchGroups.GroupB.class)
    private Date createTime;

    /**
     * 更新人
     */
    @Null(message = "查询时不可传入更新人", groups = SearchGroups.GroupC.class)
    private Long updateUser;

    /**
     * 更新时间
     */
    @Null(message = "查询时不可传入更新时间", groups = SearchGroups.GroupD.class)
    private Date updateTime;

    /**
     * 状态(1:正常,0:禁用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标识(1:已删除,0:未删除)
     */
    @Null(message = "查询时不可传入删除标识", groups = SearchGroups.GroupF.class)
    @TableLogic
    private Integer isDel;


}
