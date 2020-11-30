package com.nala.sharding.disruptor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Disruptor线程工厂；主要是给线程设置线程组和线程名；
 * 方便调试
 */

public class DisruptorThreadFactory implements ThreadFactory {

    /**
     * 自增；用于生成线程名用
     */
    private static final AtomicLong THREAD_NUMBER = new AtomicLong(1);

    /***
     * 线程组名
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("disruptor");

    /**
     * 是否为守护线程（只要非守护线程中有一个没有结束，守护线程就不会结束）
     */
    private static volatile boolean daemon;

    /**
     * 线程名前缀
     */
    private final String namePrefix;

    private DisruptorThreadFactory(final String namePrefix, final boolean daemon) {
        this.namePrefix = namePrefix;
        DisruptorThreadFactory.daemon = daemon;
    }


    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(THREAD_GROUP, runnable, THREAD_GROUP.getName() + "-" + namePrefix + "-" + THREAD_NUMBER.getAndIncrement());
        thread.setDaemon(daemon);

        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }

    /**
     * 自定义线程factory
     *
     * @param namePrefix 前缀
     * @param daemon 是否守护线程
     * @return 创建disruptor线程工厂
     */
    public static ThreadFactory create(final String namePrefix, final boolean daemon) {
        return new DisruptorThreadFactory(namePrefix, daemon);
    }
}
