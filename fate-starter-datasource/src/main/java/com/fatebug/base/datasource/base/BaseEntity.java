package com.fatebug.base.datasource.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fatebug.base.core.valid.groups.SearchGroups;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity基类
 *
 * @author fatebug
 */
@Data
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 2917589817160670213L;

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
