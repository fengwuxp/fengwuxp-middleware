package com.wind.common.util;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池创建工具
 *
 * @author wuxp
 * @date 2023-12-26 10:35
 **/
public final class ExecutorServiceUtils {

    private ExecutorServiceUtils() {
        throw new AssertionError();
    }

    /**
     * 创建一个单线程，等待队列为 128 的线程池
     *
     * @param threadNamePrefix 线程池名称前缀
     * @return 线程池
     */
    public static ThreadPoolExecutor single(String threadNamePrefix) {
        return single(threadNamePrefix, 128);
    }

    /**
     * 创建一个单线程的线程池
     *
     * @param threadNamePrefix 线程池名称前缀
     * @param workQueueSize    等待队列大小
     * @return 线程池
     */
    public static ThreadPoolExecutor single(String threadNamePrefix, int workQueueSize) {
        return newExecutor(threadNamePrefix, 1, 1, workQueueSize);
    }

    /**
     * 创建线程池，默认线程存活时间王伟 90s
     *
     * @param threadNamePrefix 线程池名称前缀
     * @param corePoolSize     核心线程数
     * @param maximumPoolSize  最大线程数
     * @param workQueueSize    等待队列大小 默认使用 {@link ArrayBlockingQueue}
     * @return 线程池
     */
    public static ThreadPoolExecutor newExecutor(String threadNamePrefix, int corePoolSize, int maximumPoolSize, int workQueueSize) {
        return newExecutor(threadNamePrefix, corePoolSize, maximumPoolSize, new ArrayBlockingQueue<>(workQueueSize));
    }

    private static ThreadPoolExecutor newExecutor(String threadNamePrefix, int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 90, TimeUnit.SECONDS, workQueue, new CustomizableThreadFactory(threadNamePrefix));
    }
}
