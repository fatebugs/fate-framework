package com.fatebug.base.auto.service;

/**
 * 可命名的接口
 * 实现类需要实现getName方法
 */
@FunctionalInterface
public interface Nameable {
    String getName();
}
