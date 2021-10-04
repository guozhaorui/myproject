package com.test.circularreference;

public class ClassB {

    private ClassC classC;

    public void setClassC(ClassC classC) {
        this.classC = classC;
    }

    public ClassB(ClassC classC) {
        this.classC = classC;
    }

}
