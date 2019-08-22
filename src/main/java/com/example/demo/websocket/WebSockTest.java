package com.example.demo.websocket;

import org.springframework.context.annotation.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import org.springframework.web.context.ContextLoader;
import org.springframework.context.ApplicationContext;

import com.example.dao.*;
import com.example.demo.service.IDataServcie;
import com.example.demo.util.ApplicationContextRegister;
import com.example.pojo.*;
import com.example.demo.util.*;

/**
 * @ServerEndPoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL连接到websocket服务器端
 */
@Component
//访问服务端的url地址
@ServerEndpoint(value = "/websocket/{id}")
public class WebSockTest {
  private static int onlineCount = 0;
  private static ConcurrentHashMap<String, WebSockTest> webSocketSet = new ConcurrentHashMap<>();
  private static ConcurrentHashMap<String, List<String>> messageCache = new ConcurrentHashMap<>();

  //与某个客户端的连接会话，需要通过它来给客户端发送数据
  private Session session;
//  private static Logger log = LogManager.getLogger(WebSockTest.class);
  private String id = "";
  //最后一次接收到心跳信息的时间
  private long lastHeartBeatTime = new Date().getTime();
  private long tomeout = 600*1000;
  private long frquency = 100*1000;
  private Timer timer = new Timer(true);
  private TimerTask timerTask = new TimerTask() { 
	  public void run(){
		  long nowTime = new Date().getTime();
		  System.out.println("TimerTask执行，当前时间：" + nowTime + "， 当前id：" + id);
		  if(nowTime - lastHeartBeatTime > tomeout) {
			  System.out.println("=================================== 超时未收到心跳信息，用户" + id + "下线 ->->->...");
			  webSocketSet.remove(id);  //从set中删除
		      subOnlineCount();           //在线数减1
		      System.out.println("websocket剩余连接：" + webSocketSet.keySet());
		      timer.cancel();
		  }
		  else {
			  lastHeartBeatTime = nowTime;
		  }
	  } 
  };
  
  private ApplicationContext act = ApplicationContextRegister.getApplicationContext();
  private IDataServcie dataService = act.getBean(IDataServcie.class);
	
  /**
   * 连接建立成功调用的方法*/
  @OnOpen
  public void onOpen(@PathParam(value = "id") String id, Session session) {
      this.session = session;
      this.id = id;//接收到发送消息的人员编号
      System.out.println("onOpen-id: " + id);
      //检查用户名是否合法
      if(id == null || id.equals("null") || id=="") {
    	  return;
      }
      //先检查是否存在同名websocket，如果有则删除旧的换成新的
      if(webSocketSet.containsKey(id)) {
    	  webSocketSet.remove(id);
      }
      webSocketSet.put(id, this);     //加入set中
      addOnlineCount();           //在线数加1
      System.out.println("用户"+id+"加入！当前在线人数为" + getOnlineCount());
      System.out.println("当前的用户有： " + webSocketSet.keySet());
      try {
//        sendMessage("连接成功");
//    	  getMessageCache();
    	  if(messageCache.containsKey(id)) {
    		  System.out.println("用户 " + id + "有未查看的消息！");
    		  List<String> messages = messageCache.get(id);
//    		  System.out.println("=== onOpen ===   2");
    		  for(String message: messages) {
    			  System.out.println(message);
    			  sendtoUser(message,id);
    		  }
    		  messageCache.remove(id);
//    		  System.out.println("=== onOpen ===   3");
    	  }
    	  else {
//    		  System.out.println("messageCache不包含" + id);
//    		  System.out.println(messageCache.contains(id));
    	  }

    	  lastHeartBeatTime = new Date().getTime();
    	  timer.schedule(timerTask, 0, frquency);
    	  System.out.println("连接成功");
      } 
      catch (Exception e) {
    	  System.out.println("websocket IO异常");
      }
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose() {
      webSocketSet.remove(id);  //从set中删除
      subOnlineCount();           //在线数减1
      System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
      timer.cancel();
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息*/
  @OnMessage
  public void onMessage(String message, Session session) {
//      System.out.println("来自客户端的消息:" + message);
      lastHeartBeatTime = new Date().getTime();
      //先检查是不是心跳信息
      if(message.equals("HeartBeat")) {
//    	  System.out.println("=========================== HeartBeat ========================== bom,bom,bom!");
    	  try {
    		  //发送给自己
	    	  sendtoUser("HeartBeat received.",id);
	      }
    	  catch(Exception e) {
	    	  e.printStackTrace();
	      }
      }
      else {
	      JSONObject json_test = JSONObject.parseObject(message);
	      Record record = new Record();
	      record.makeARecordFromJSON(json_test);
	      
	      try {
	    	  sendtoUser(message,record.getToName());
	      }catch(Exception e) {
	    	  e.printStackTrace();
	    	  webSocketSet.remove(record.getToName());
	      }
      }
      //可以自己约定字符串内容，比如 内容|0 表示信息群发，内容|X 表示信息发给id为X的用户
//      String sendMessage = message.split("[|]")[0];
//      String sendUserId = message.split("[|]")[1];
      
//      try {
//          if(sendUserId.equals("0"))
//              sendtoAll(sendMessage);
//          else
//              sendtoUser(sendMessage,sendUserId);
////        	  sendtoUser(message, sendUserId);
//      } catch (IOException e) {
//          e.printStackTrace();
//      }
  }

  /**
   *
   * @param session
   * @param error
   */
  @OnError
  public void onError(Session session, Throwable error) {
      System.out.println("发生错误");
      error.printStackTrace();
  }


  public void sendMessage(String message) throws IOException {
      this.session.getBasicRemote().sendText(message);
  }

  /**
   * 发送信息给指定ID用户，如果用户不在线则返回不在线信息给自己
   * @param message
   * @param sendUserId
   * @throws IOException
   */
  public void sendtoUser(String message,String sendUserId) throws IOException {	  
//	  System.out.println("=========== sendtoUser -> " + sendUserId + " ==========");
	  try {
      if (webSocketSet.get(sendUserId) != null) {
          if(!id.equals(sendUserId)) {
				webSocketSet.get(sendUserId).sendMessage(message);
				
          }
          else
          {
        	  webSocketSet.get(sendUserId).sendMessage(message);
          }
      } else {
          //如果用户不在线则将该信息存入messageCache
    	  setMessageCache(sendUserId, message);
      }
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  
//  public boolean insertToRecords(String name, String toName, String message, String chatImg) throws IOException {	  
//	  	System.out.println("=========== websocket insertToRecords ==========");
//	  	boolean flag = false;
//	  	
//	  	if(dataService == null) {
//	  		System.out.println("dataService == null ....");	
//	  	}
//	  	else {
//	  		System.out.println("dataService is not null!");
//	  		flag = dataService.addNewRecord(message, toName, name, chatImg);
//	  	}	
//	  	return flag;
//  }

  /**
   * 发送信息给所有人
   * @param message
   * @throws IOException
   */
  public void sendtoAll(String message) throws IOException {
      for (String key : webSocketSet.keySet()) {
          try {
              webSocketSet.get(key).sendMessage(message);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
  
  /**
   * set messageCache
   * @param id, record
   * @throws IOException
   */
  public void setMessageCache(String id, String message) throws IOException {
	  System.out.println("setMessageCache: {" + id + ", " + message + "}");	
	  if(!messageCache.containsKey(id)) {
//		  System.out.println("setMessageCache 1");
		  List<String> listRec = new ArrayList<String>();
		  listRec.add(message);
		  messageCache.put(id, listRec);
	  }
	  else {
//		  System.out.println("setMessageCache 2");
		  List<String> listRec = messageCache.get(id);
//		  System.out.println("before: ");
//		  System.out.println(listRec);
		  listRec.add(message);
//		  System.out.println("after: ");
//		  System.out.println(listRec);
		  messageCache.put(id, listRec);
	  }
//	  getMessageCache();
  }
  
  /**
   * set messageCache
   * @param id, record
   * @throws IOException
   */
  public void getMessageCache() throws IOException {
	  System.out.println("getMessageCache: ");
	  System.out.println("size: " + messageCache.size());
	  System.out.println(messageCache);
  }
  
  public void clearAll() throws Exception{
	  webSocketSet.clear();
  }


  public static synchronized int getOnlineCount() {
//      return onlineCount;
	  return webSocketSet.size();
  }

  public static synchronized void addOnlineCount() {
      WebSockTest.onlineCount++;
  }

  public static synchronized void subOnlineCount() {
      WebSockTest.onlineCount--;
  }
}

