package com.wind.common.locks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于 jdk 的锁实现
 *
 * @author wuxp
 * @date 2023-11-14 08:48
 **/
public class JdkLockFactory implements LockFactory {

    /**
     * @key 锁标识
     * @value Lock
     */
    private final Cache<String, WindLock> locks = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(5000)
            .build();

    @Override
    public WindLock apply(String key) {
        try {
            return locks.get(key, WindReentrantLock::new);
        } catch (ExecutionException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, String.format("get lock failure：%s", exception.getMessage()), exception);
        }
    }

    private static final class WindReentrantLock extends ReentrantLock implements WindLock {

        @Override
        public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
          return tryLock(waitTime,unit);
        }

        @Override
        public void lock(long leaseTime, TimeUnit unit) {
            lock();
        }
    }
}
