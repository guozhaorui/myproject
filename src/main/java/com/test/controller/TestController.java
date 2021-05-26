package com.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.service.TestService;

@RestController
@RequestMapping(value = "/test", produces = {"application/json;charset=utf-8"})
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/query")
    public void test() {
        Object object = testService.query();
        System.out.println(object);
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
