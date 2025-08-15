package com.fatebug.base.datasource.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fatebug.base.core.exception.FateException;
import com.fatebug.base.datasource.param.PageData;
import com.fatebug.base.datasource.param.SuperQuery;
import com.fatebug.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class MybatisPlusUtils {

    /**
     * 一步到位，获取mybatis plus中的QueryWrapper
     *
     * @param page 分页条件
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> getQueryWrapper(PageData<T> page) {
        QueryWrapper<T> queryWrapper = getQueryWrapper(page.getEntity());
        return getQueryWrapperPage(queryWrapper, page);
    }

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param entity 实体
     * @param <T>    类型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param page 条件
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> getQueryWrapperPage(PageData<T> page) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        return getQueryWrapperPage(queryWrapper, page);
    }

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param queryWrapper 查询条件
     * @param page         分页条件
     * @param <T>          类型
     * @return QueryWrapper 返回查询条件
     */
    public static <T> QueryWrapper<T> getQueryWrapperPage(QueryWrapper<T> queryWrapper, PageData<T> page) {
        //遍历新的内容
        List<SuperQuery> list = page.getParams();
        //排序
//        List<String> sort = page.getSort();
        //迭代
        if (Optional.ofNullable(list).isPresent() && !list.isEmpty()) {
            for (SuperQuery superQuery : list) {
                // 文本框
                if ("input".equals(superQuery.getType()) && StrUtil.isNotBlank(superQuery.getVal())) {
                    if ("eq".equals(superQuery.getRule())) {
                        queryWrapper.eq(superQuery.getField(), superQuery.getVal());
                    } else if ("like".equals(superQuery.getRule())) {
                        queryWrapper.like(superQuery.getField(), superQuery.getVal());
                    } else if ("ne".equals(superQuery.getRule())) {
                        queryWrapper.ne(superQuery.getField(), superQuery.getVal());
                    } else if ("ni".equals(superQuery.getRule())) {
                        queryWrapper.notIn(superQuery.getField(), superQuery.getVal());
                    } else if ("in".equals(superQuery.getRule())) {
                        String val = superQuery.getVal();
                        String[] vals = val.split(",");
                        queryWrapper.in(superQuery.getField(), (Object) vals);
                    }
                } else if ("date".equals(superQuery.getType()) && StrUtil.isNotBlank(superQuery.getVal())) {
                    // 日期
                    if ("bt".equals(superQuery.getRule())) {// 日期区间
                        String[] dateArray = null;
                        dateArray = superQuery.getVal().split(",");
                        try {
                            queryWrapper.apply(
                                    String.format(
                                            "to_char(%s, 'yyyy-MM-dd') BETWEEN '%s' AND '%s'",
                                            superQuery.getField(),
                                            dateArray[0],
                                            dateArray[1]
                                    )
                            );
                        } catch (Exception e) {
                            log.error("日期区间转换异常", e);
                            throw new FateException("日期区间转换异常");
                        }
                    } else if ("eq".equals(superQuery.getRule())) {// 日期等于
                        queryWrapper.apply(
                                String.format(
                                        "to_char(%s, 'yyyy-MM-dd') = to_char(TO_DATE('%s', 'yyyy-MM-dd'), 'yyyy-MM-dd')",
                                        superQuery.getField(),
                                        superQuery.getVal()
                                )
                        );
                    }
                }
            }
        }
        //排序,遍历集合
//        if (page.isOrderBy() && ObjectUtil.isNotEmpty(sort)) {
//            queryWrapper.orderByAsc(sort);
//        } else {
//            queryWrapper.orderByDesc(sort);
//        }
        return queryWrapper;
    }

    /**
     * 转化成mybatis plus中的Page
     *
     * @param pageData 查询条件
     * @return IPage
     */
    public static <T> IPage<T> getPage(PageData<T> pageData) {
        Page<T> page = new Page<>(pageData.getCurrent(), pageData.getSize());
        if (pageData.getSize() == -1) {
            page.setMaxLimit(250000L);
        }

        for (String asc : pageData.getAscSort()) {
            page.addOrder(OrderItem.asc(StringUtils.cleanIdentifier(asc)));
        }
        for (String desc : pageData.getDescSort()) {
            page.addOrder(OrderItem.desc(StringUtils.cleanIdentifier(desc)));
        }
        return page;
    }
}
