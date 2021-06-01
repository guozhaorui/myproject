package com.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.service.TestService;

@RestController
@RequestMapping(value = "/test", produces = {"application/json;charset=utf-8"})
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

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
}
