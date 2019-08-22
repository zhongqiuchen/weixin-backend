package com.example.dao;

import com.example.pojo.TokenEntity;

public interface TokenEntityMapper {
    int deleteByPrimaryKey(Integer tokenId);
    
    int deleteByName(String userName);

    int insert(TokenEntity record);

    int insertSelective(TokenEntity record);

    TokenEntity selectByPrimaryKey(Integer tokenId);
    
    TokenEntity selectByName(String userName);

    int updateByPrimaryKeySelective(TokenEntity record);

    int updateByPrimaryKey(TokenEntity record);
}