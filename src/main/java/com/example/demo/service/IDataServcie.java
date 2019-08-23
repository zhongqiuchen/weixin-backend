package com.example.demo.service;

import java.util.List;

import com.example.pojo.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IDataServcie {
	
	public List<Record> getAllRecords(String userName);
	public List<Record> getSomeRecords(String userName, String toName, int num1, int num2);
	public boolean addNewRecord(String message, String toName, String name, String chatImg);

}
