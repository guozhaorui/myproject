package com.test.controller;

import com.test.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test", produces = {"application/json;charset=utf-8"})
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

    //  private static Object lock = new Object();

    private static int m = 0;

    @RequestMapping("/query")
    public void test() {
        LOGGER.info("开始");
        Object object = testService.query();
        System.out.println(object);
        LOGGER.info("结束");
    }

    @RequestMapping("/startAddPerson")
    public void startAddPerson() {
        testService.startInsert();
    }

    @RequestMapping("/testDbLock")
    public void testDbLock() {
        testService.testDbLock();
    }

    @RequestMapping("/testConcurrent")
    public void testConcurrent() throws InterruptedException {
        //    synchronized (lock) {
        m++;
        Thread.sleep(100);
        System.out.println(m);
        //   }
    }
}
