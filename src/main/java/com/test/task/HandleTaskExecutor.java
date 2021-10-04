package com.test.task; /**
 * Copyright(C) 2018 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */


import com.test.task.handle.AbstractHandler;
import org.springframework.core.task.TaskRejectedException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用线程池任务处理执行器
 *
 * @since 2018年5月16日 下午1:32:01
 * @author yly
 *
 */
public class HandleTaskExecutor {

    /**
     * 核心线程大小
     */
    private static int CORE_POOL_SIZE = 4;
    /**
     * 线程池最大线程数
     */
    private static int MAX_POOL_SIZE = 16;
    /**
     * 额外线程空状态生存时间
     */
    private static int KEEP_ALIVE_TIME = 30 * 1000;
    /**
     * 队列大小
     */
    private static int MAX_QUEUE_SIZE = 30000;

    private HandleTaskExecutor() {
    }

    private static class TmcHandleTaskExecutorHolder {
        static HandleTaskExecutor me = new HandleTaskExecutor();
    }

    /**
     * 拿到执行器实例
     * @return
     */
    public static HandleTaskExecutor me() {
        return TmcHandleTaskExecutorHolder.me;
    }

    /**
     * 线程池
     */
    private static ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE),
            new ThreadFactory() {

                private final AtomicInteger index = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    String threadName = "任务处理线程[HandleTaskExecutor]-" + (null != r ? r.getClass().getSimpleName() : "") + "#" + index.getAndIncrement();
                    return new Thread(r, threadName);
                }

            }, new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 线程池过载 抛出异常
            String info = "";
            if (r instanceof AbstractHandler) {
                AbstractHandler task = (AbstractHandler) r;
                info = task.rejected();
            }
            throw new TaskRejectedException(String.format("线程池过载，任务被拒绝。%s", info));
        }

    });

    /**
    /**
     * 添加任务
     */
    public void execute(AbstractHandler handleTask) throws Exception {
        THREAD_POOL.execute(handleTask);
    }

}
