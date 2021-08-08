package com.test.start;

import java.util.ArrayList;
import java.util.List;

public class BaseTaskServiceFactory {
    /**
     * 任务集合
     */
    protected List<ITaskHandle> taskHandleList;

    /**
     * 构造器
     */
    BaseTaskServiceFactory() {
        taskHandleList = new ArrayList<>();
    }

    public void startTask() {
        if (this.taskHandleList.size() == 0)
            return;
        for (ITaskHandle task : taskHandleList) {
            if (!task.isNeedDo()) {
                continue;
            }
            Thread thd = new Thread(() -> {
                task.doTask();
            }
            );
            thd.start();
        }
    }

    /**
     * 注册任务
     *
     * @param taskHandle
     */
    protected void register(ITaskHandle taskHandle) {
        this.taskHandleList.add(taskHandle);
    }
}
