package com.test.mytest.poxy;

public class PoxyObject implements IPoxyTest {

    private RealObject object;

    @Override
    public void testDo() {
        if (null == object) {
            object = new RealObject();
        }
        System.out.println("我来代理");

        object.testDo();
    }
}
