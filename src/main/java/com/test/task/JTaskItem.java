package com.test.task;

/**
 * Copyright(C) 2018 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 异步任务项。
 *
 * @author 洪光旺
 * @since 2018年3月12日 下午5:07:20
 */
public class JTaskItem {
    /**
     * 构造器。
     *
     * @param es               ExecutorService对象
     * @param funTaskRun       任务执行方法
     * @param funTaskBeforeRun 在funTaskRun执行前的方法
     * @param funTaskCompleted 在funTaskRun执行完成后的方法(不管funTaskRun执行成功、失败或发生异常都触发)
     * @param arg              任务执行参数
     */
    public JTaskItem(ExecutorService es, Function<Object, Object> funTaskRun, Consumer<Object> funTaskBeforeRun, Consumer<Object> funTaskCompleted, Object arg) {
        this.future = es.submit(() -> {
            Object result = null;
            try {
                //在funTaskRun执行前的方法。
                if (null != funTaskBeforeRun) {
                    funTaskBeforeRun.accept(arg);
                }

                //执行主体方法。
                result = funTaskRun.apply(arg);
            } catch (Exception ex) {
                throw ex;
            } finally {
                //在funTaskRun执行完成后的方法。
                if (null != funTaskCompleted) {
                    funTaskCompleted.accept(arg);
                }
            }
            return result;
        });
    }

    /**
     * 构造器。
     *
     * @param es       ExecutorService对象
     * @param callable callable方法
     */
    public <T> JTaskItem(ExecutorService es, Callable<T> callable) {
        this.future = (Future<Object>) es.submit(callable);
    }

    /**
     * 任务执行参数
     */
    private Object arg;
    /**
     * Future对象
     */
    private Future<Object> future;
    /**
     * 任务执行返回结果
     */
    private Object result;

    /**
     * 任务执行是否完成。
     *
     * @return
     */
    public boolean isDone() {
        return this.future.isDone();
    }

    /**
     * 获取任务执行结果。
     *
     * @return
     */
    public Object getResult() {
        if (null == this.future) {
            return this.result;
        }

        try {
            this.result = this.future.get();
        } catch (Exception e) {

        } finally {
            this.future.cancel(true);
            this.future = null;
        }

        return this.result;
    }
}
