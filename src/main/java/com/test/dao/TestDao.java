package com.test.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.test.model.Person;

public interface TestDao {

    @Select("select * from test")
    public List<Object> query();

    @Update("update worker_table set  `desc` = '1123411' ; ")
    public int updateTest();

    @Update("update person set  problem = '12测试1122111' where person_id=1164437474429380992 ; ")
    public int updatePerson();

    /**
     * 批量插入人员信息
     * @param personList
     */
    public int batchAddPerson(@Param("personList") List<Person> personList);

}
