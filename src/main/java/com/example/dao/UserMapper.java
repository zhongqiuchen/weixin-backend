package com.example.dao;

import com.example.pojo.User;
import com.example.pojo.UserKey;

public interface UserMapper {
    int deleteByPrimaryKey(UserKey key);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(UserKey key);
    
    User selectByUserPwd(String username, String password);
    
    User selectByName(String username);
    
    User selectByNickname(String nickname);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}