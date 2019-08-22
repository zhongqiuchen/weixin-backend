package com.example.pojo;

import java.util.Date;
import com.alibaba.fastjson.JSONObject;

public class Record {
    private Integer id;

    private String name;

    private String toName;

    private String words;

    private String chatimg;

    private Date time;
    
    private String table;
    
    public void makeARecord(Integer id, String name, String toName, String words, String chatimg, String time, String table){
    	this.id = id;
    	this.name = name;
    	this.toName = toName;
    	this.words = words;
    	this.chatimg = chatimg;
    	this.time = new Date();
    	this.table = table;
    }
    
    public void makeARecordFromJSON(JSONObject json){
    	if(json != null) {
    		this.id = (Integer)json.get("id");
        	this.name = (String)json.get("name");
        	this.toName = (String)json.get("toName");
        	this.words = (String)json.get("words");
        	this.chatimg = (String)json.get("chatimg");
        	this.time = new Date();
        	this.table = (String)json.get("table");
    	}
    }
    
    public String getTable() {
    	return table;
    }
    
    public void setTable(String table) {
    	this.table = table;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName == null ? null : toName.trim();
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words == null ? null : words.trim();
    }

    public String getChatimg() {
        return chatimg;
    }

    public void setChatimg(String chatimg) {
        this.chatimg = chatimg == null ? null : chatimg.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}