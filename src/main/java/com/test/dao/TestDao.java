package com.test.dao;

import com.test.model.Person;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("testDao")
public interface TestDao {

    @Select("select * from test")
    public List<Object> query();

    @Update("update worker_table set  `desc` = '1123411' ; ")
    public int updateTest();

    @Update("update person set  problem = '12测试1122111' where person_id=1164437474429380992 ; ")
    public int updatePerson();

    @Update("update person set  person_work=8  where  person_id=1164437366400887168 ")
    public void updatePerson1();

    @Update("update person set  person_work=3  where problem like '%发%'  ")
    public int updatePerson2();

    /**
     * 批量插入人员信息
     *
     * @param personList
     */
    public int batchAddPerson(@Param("personList") List<Person> personList);

}
