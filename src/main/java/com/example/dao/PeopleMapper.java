package com.example.dao;

import com.example.pojo.People;
import java.util.List;

public interface PeopleMapper {
    int deleteByPrimaryKey(String name);

    int insert(People record);

    int insertSelective(People record);

    People selectByPrimaryKey(String name);
    
    List<People> selectAll();

    int updateByPrimaryKeySelective(People record);

    int updateByPrimaryKey(People record);
}