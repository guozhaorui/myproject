package com.test.start;


import com.test.util.SpringResolveManager;
import org.springframework.stereotype.Component;

@Component
public class AppTaskServiceFactory extends BaseTaskServiceFactory {

    AppTaskServiceFactory() {
        super();
        //  ITaskHandle task = SpringResolveManager.resolve(TaskOneHandle.class);
        //   this.register(task);
        //  this.register(SpringResolveManager.resolve(TaskTwoHandle.class));
    }

    public static AppTaskServiceFactory get() {
        return SpringResolveManager.resolve(AppTaskServiceFactory.class);
    }
}
