package com.example.dao;

import com.example.pojo.Record;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Record record);
    
    int insertInTable(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Integer id);
    
    List<Record> selectAll(@Param("value")String value);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);
}