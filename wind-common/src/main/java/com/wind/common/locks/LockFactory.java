package com.wind.common.locks;


import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * 锁创建工厂
 *
 * @author wuxp
 * @date 2023-11-14 08:47
 **/
public interface LockFactory extends Function<String, Lock> {
}