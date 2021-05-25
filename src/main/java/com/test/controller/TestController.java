package com.test.controller;

import com.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
