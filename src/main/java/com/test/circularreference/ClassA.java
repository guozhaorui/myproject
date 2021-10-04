package com.test.circularreference;


public class ClassA {

    private ClassB classB;

    public void setClassB(ClassB classB) {
        this.classB = classB;
    }

    public ClassA(ClassB classB) {
        this.classB = classB;
    }
}
