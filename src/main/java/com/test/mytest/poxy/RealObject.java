package com.test.mytest.poxy;

public class RealObject implements IPoxyTest {

    @Override
    public void testDo() {
        System.out.println("我是真正要做事情的");
    }
}
