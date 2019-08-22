package com.example.demo.service;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

import com.example.pojo.*;

@Mapper
public interface ITokenOperationService {
	
	public Map<String, Object> login(String username, String password);
	
	public Map<String, TokenEntity> updateToken(String userName);
	
	public Map<String, Object> operateToKen(Map<String, Object> map, User user, String userName);
	
	public String creatToken(String userName, Date date);

}
