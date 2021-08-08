package com.test.start.plugins;

import com.test.start.ITaskHandle;
import org.springframework.stereotype.Service;

@Service
public class TaskTwoHandle implements ITaskHandle {

    public void TaskTwoHandle() {

    }

    @Override
    public void doTask() {
        System.out.println("任务2开始执行");
    }

    @Override
    public boolean isNeedDo() {
        return true;
    }
}
