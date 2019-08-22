package com.example.dao;

import com.example.pojo.Friend;
import java.util.List;

public interface FriendMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Friend record);

    int insertSelective(Friend record);

    Friend selectByPrimaryKey(Integer id);
    
    List<Friend> selectByNums(Integer num1, Integer num2);

    int updateByPrimaryKeySelective(Friend record);

    int updateByPrimaryKey(Friend record);
}