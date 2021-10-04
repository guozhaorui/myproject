package com.test.circularreference;

public class ClassC {

    private ClassA classA;

    public void setClassA(ClassA classA) {
        this.classA = classA;
    }

    public ClassC(ClassA classA) {
        this.classA = classA;
    }
}
