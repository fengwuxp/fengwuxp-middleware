package com.wind.common.locks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于 jdk 的锁实现
 *
 * @author wuxp
 * @date 2023-11-14 08:48
 **/
public class SimpleLockFactory implements LockFactory {

    /**
     * @key 锁标识
     * @value Lock
     */
    private final Cache<String, Lock> locks = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(5000)
            .build();

    @Override
    public Lock apply(String key) {
        try {
            return locks.get(key, ReentrantLock::new);
        } catch (ExecutionException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, String.format("get lock failure：%s", exception.getMessage()), exception);
        }
    }
}
