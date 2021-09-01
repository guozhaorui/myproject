package com.test.mytest.other;

import java.util.Arrays;
import java.util.List;

public class TestClass {

    public static void main(String[] args) {
        List<String> ass = Arrays.asList("111", "222", "22333");
        TestInterface test = new Test1();

        ass.forEach(g -> {
            test.testMethod(g);
        });
    }

}
