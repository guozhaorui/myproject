/**
 * Copyright(C) 2021 Hangzhou Differsoft Co., Ltd. All rights reserved.
 *
 */
package com.test.task;

import com.test.service.TestService;
import com.test.util.SpringResolveManager;

/**
 * @since 2021年5月26日 下午3:03:38
 * @author guozr
 *
 */
public class MyTask implements Runnable {

    @Override
    public void run() {
        try {
            SpringResolveManager.resolve(TestService.class).updateTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
