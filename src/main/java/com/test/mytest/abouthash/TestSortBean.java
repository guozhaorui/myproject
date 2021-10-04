package com.test.mytest.abouthash;

public class TestSortBean implements Comparable {

    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public TestSortBean(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Object o) {

        TestSortBean e = (TestSortBean) o;
        if (e.getAge() > this.getAge()) {
            return 1;
        } else if (e.getAge() == this.getAge()) {
            return 0;
        } else {
            return -1;
        }
    }
}
