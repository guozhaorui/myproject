package com.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TestDao {
    @Select("select * from test")
    public List<Object> query();

}
