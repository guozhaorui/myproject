package com.test.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Copyright(C) 2018 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.test.util.SpringResolveManager;

/**
 * 封装一个异步执行同步返回结果对象(非单例)。
 *
 * @author 洪光旺
 * @since 2018年3月12日 下午3:49:02
 */
@Component
@Scope("prototype")
public class JTask {
    //region 构造器

    /**
     * 构造器(利用缓存的线程)。
     */
    public JTask() {
        this.es = Executors.newCachedThreadPool();
        this.tasks = new ArrayList<>();
        this.numRunningTask = new AtomicInteger();
    }

    /**
     * 构造器(固定线程数)。
     *
     * @param nThreads 固定线程数
     */
    public JTask(int nThreads) {
        this.es = Executors.newFixedThreadPool(nThreads);
        this.tasks = new ArrayList<>();
        this.numRunningTask = new AtomicInteger();
    }

    //endregion

    //region 变量

    /**
     * ExecutorService对象
     */
    private ExecutorService es = null;
    /**
     * 执行结果集。
     */
    private List<JTaskItem> tasks = null;
    /**
     * 线程池中的任务数(线程安全)。
     */
    private AtomicInteger numRunningTask = null;

    //endregion

    //region 公共方法

    /**
     * 添加并启动任务(短生命周期方法，必须调用dispose方法释放对象)。
     *
     * @param funTaskRun 任务执行方法
     * @param arg        任务执行参数
     */
    public void startTask(Object arg, Function<Object, Object> funTaskRun) {
        JTaskItem task = new JTaskItem(this.es, funTaskRun, null, null, arg);
        this.tasks.add(task);
    }

    /**
     * 添加并启动任务(短生命周期方法，必须调用dispose方法释放对象)。
     *
     * @param callable callable方法
     */
    public <T> void startTask(Callable<T> callable) {
        JTaskItem task = new JTaskItem(this.es, callable);
        this.tasks.add(task);
    }

    /**
     * 添加并启动任务(长生命周期方法)。
     *
     * @param arg        任务执行参数
     * @param funTaskRun 任务执行方法
     */
    public void runTask(Object arg, Function<Object, Object> funTaskRun) {
        this.runTask(arg, funTaskRun, null, null);
    }

    /**
     * 添加并启动任务(长生命周期方法)。
     *
     * @param arg              任务执行参数
     * @param funTaskRun       任务执行方法
     * @param funTaskBeforeRun 在funTaskRun执行前的方法
     * @param funTaskCompleted 在funTaskRun执行完成后的方法(不管funTaskRun执行成功、失败或发生异常都触发)
     */
    public void runTask(Object arg, Function<Object, Object> funTaskRun, Consumer<Object> funTaskBeforeRun, Consumer<Object> funTaskCompleted) {
        //创建任务对象。
        JTaskItem task = new JTaskItem(this.es, funTaskRun, (g) -> {
            //在funTaskRun执行前的方法

            //线程池任务数加1。
            this.numRunningTask.incrementAndGet();

            //执行funTaskBeforeRun方法。
            if (null != funTaskBeforeRun) {
                funTaskBeforeRun.accept(g);
            }
        }, (g) -> {
            //在funTaskRun执行完成后的方法

            //线程池任务数减1。
            this.numRunningTask.decrementAndGet();

            //执行funTaskCompleted方法。
            if (null != funTaskCompleted) {
                funTaskCompleted.accept(g);
            }
        }, arg);
    }

    /**
     * 等待所有任务执行完毕。
     */
    public void waitAll() {
        this.waitAll(0);
    }

    /**
     * 等待所有任务执行完毕。
     *
     * @param timeout 超时时间(单位：毫秒)
     */
    public void waitAll(long timeout) {
        try {
            //通知线程池将要关闭，线程池拒接收新提交的任务，同时等待线程池⾥的任务执行完毕后关闭线程池。
            this.es.shutdown();
            // 等待线程池中的任务完成直至超时(以设置超时时间)。
            this.es.awaitTermination(timeout > 0 ? timeout : 30000, TimeUnit.MILLISECONDS);

            // 获取任务执行结果。
            for (JTaskItem taskItem : this.tasks) {
                //如果任务已经完成则获取执行结果(未完成表示等待超时)。
                if (taskItem.isDone()) {
                    taskItem.getResult();
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 获取运行结果。
     *
     * @return
     */
    public Object[] getResult() {
        return this.tasks.stream().map(JTaskItem::getResult).toArray();
    }

    /**
     * 获取线程池中的任务数。
     *
     * @return
     */
    public int getRunningTaskNumber() {
        return this.numRunningTask.get();
    }

    /**
     * 释放资源。
     */
    public void dispose() {
        //关闭线程池。
        if (!this.es.isShutdown()) {
            this.es.shutdownNow();
        }

        //释放集合。
        if (null != this.tasks) {
            this.tasks.clear();
            this.tasks = null;
        }
    }

    //endregion

    // region 创建对象实例

    /**
     * 创建对象实例(固定线程数)。
     *
     * @param nThreads 固定线程数
     * @return 对象实例
     */
    public static JTask createInstance(int nThreads) {
        return SpringResolveManager.resolve(JTask.class, nThreads);
    }

    /**
     * 创建对象实例(非固定线程数)。
     *
     * @return 对象实例
     */
    public static JTask createInstance() {
        return SpringResolveManager.resolve(JTask.class);
    }

    // endregion
}
