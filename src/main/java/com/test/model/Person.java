/**
 * Copyright(C) 2021 Hangzhou Differsoft Co., Ltd. All rights reserved.
 *
 */
package com.test.model;

/**
 * @since 2021年5月26日 上午11:12:33
 * @author guozr
 *
 */
public class Person {

    private Integer id;

    private Long personId;

    private String personName;

    private Byte personSex;

    private Byte personAge;

    private Byte personWork;

    private String problem;

    /**
     * @param personId
     * @param personName
     * @param personSex
     * @param personAge
     * @param personWork
     */
    public Person(Long personId, String personName, Byte personSex, Byte personAge, Byte personWork, String problem) {
        super();
        this.personId = personId;
        this.personName = personName;
        this.personSex = personSex;
        this.personAge = personAge;
        this.personWork = personWork;
        this.problem = problem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Byte getPersonSex() {
        return personSex;
    }

    public void setPersonSex(Byte personSex) {
        this.personSex = personSex;
    }

    public Byte getPersonAge() {
        return personAge;
    }

    public void setPersonAge(Byte personAge) {
        this.personAge = personAge;
    }

    public Byte getPersonWork() {
        return personWork;
    }

    public void setPersonWork(Byte personWork) {
        this.personWork = personWork;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

}
