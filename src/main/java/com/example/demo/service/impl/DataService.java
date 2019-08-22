package com.example.demo.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.example.dao.FriendMapper;
import com.example.dao.PeopleMapper;
import com.example.dao.RecordMapper;
import com.example.dao.UserMapper;
import com.example.demo.service.*;
import com.example.pojo.*;

@Service("dataService")
public class DataService implements IDataServcie{
	
	@Resource
	private UserMapper userMapper;
		
	@Resource
	private PeopleMapper peopleMapper;
	
	@Resource
	private FriendMapper friendMapper;
	
	@Resource
	private RecordMapper recordMapper;
	
	public List<Record> getAllRecords(String userName) {
		// TODO Auto-generated method stub
		if(userName != null) {
			String table = "general_chat_" + userName;
	    	List<Record> records = recordMapper.selectAll(table);
	    	return records;
		}
		else {
			System.out.println("getAllRecords fail, userName = " + userName);
			return null;
		}
	}
	
	public boolean addNewRecord(String message, String toName, String name, String chatImg){
    	Date newDate = new Date();
    	int result_1 = 0, result_2 = 0, result_3 = 0;
    	boolean flag = false;
    	System.out.println("---------------------addNewRecord-------------------");
    	System.out.println(name + " -> " + toName);
    	System.out.println("message: " + message);
    	System.out.println("chatImg: " + chatImg);
    	System.out.println(newDate);
    	
    	try {
	    	//先发送给总表
	    	Record newRecord = new Record();
	    	newRecord.setName(name);
	    	newRecord.setWords(message);
	    	newRecord.setToName(toName);
	    	newRecord.setTime(newDate);
	    	newRecord.setChatimg(chatImg);
	    	newRecord.setTable("general_chat_records");
	    	
	    	result_1 = recordMapper.insertInTable(newRecord);
	    	System.out.println("result1 is: " + result_1);
	    	
	    	//再发送给发信人表
	    	newRecord.setName("me");
	    	newRecord.setToName(toName);
	    	newRecord.setTable("general_chat_" + name);
	    	result_2 = recordMapper.insertInTable(newRecord);
	    	System.out.println("result2 is: " + result_2);
	    	
	    	//发送给收信人表
	    	newRecord.setName(name);
	    	newRecord.setToName("me");
	    	newRecord.setTable("general_chat_" + toName);
	    	result_3 = recordMapper.insertInTable(newRecord);
	    	System.out.println("result3 is: " + result_3);
	    	
	    	if(result_1==1 && result_2==1 && result_3==1) {
	    		flag = true;
	    	}
	    	System.out.println("addNewRecord ok");
    	}
    	catch(Exception e){
    		System.out.println("addNewRecord error!!!");
    	}
    	
//    	//最后将更新后的数据返回给前端
//    	String table = "general_chat_" + name;
//    	List<Record> records = recordMapper.selectAll(table);
    	System.out.println("addNewRecord success");
    	return flag;
	}
	


}
