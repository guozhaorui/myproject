package com.test.start.plugins;

import com.test.start.ITaskHandle;
import org.springframework.stereotype.Service;

@Service
public class TaskOneHandle implements ITaskHandle {

    public void TaskOneHandle() {

    }

    @Override
    public void doTask() {

        System.out.println("任务1开始执行");

    }

    @Override
    public boolean isNeedDo() {
        return true;
    }
}
