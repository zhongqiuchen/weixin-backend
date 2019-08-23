package com.example.demo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;

import com.example.dao.*;
import com.example.demo.service.*;
import com.example.pojo.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.*;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

@RestController
public class SampleController {
	
	@Resource
	private UserMapper userMapper;
	
	@Resource
	private PeopleMapper peopleMapper;
	
	@Resource
	private FriendMapper friendMapper;
	
	@Resource
	private RecordMapper recordMapper;
	
	@Resource
	private IDataServcie dataService;
	
	@Resource
	private ITokenOperationService tokenOperationService;
	
	private String localPath = "C:\\Users\\18217\\Desktop\\weixin-0812-token\\";
	
    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
    
    @RequestMapping(value="/user",method= RequestMethod.POST)
    public User login(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== user =====================");
    	String username = request.getParameter("username");
    	String password = request.getParameter("password");
    	boolean flag = false;
    	
    	System.out.println(username + ", " + password);
    	
    	User user = userMapper.selectByUserPwd(username, password);
    	
    	if(user != null) {
    		System.out.println("Login Success!");
    		System.out.println(user.getName());
    		System.out.println(user.getPwd());
    	}
    	else{
    		System.out.println("Login Fail!");
    	}
    	
    	return user;
    }
    
  @RequestMapping(value="/login_with_token",method= RequestMethod.POST)
  public Map<String, Object> loginWithToken(HttpServletRequest request, HttpServletResponse response) {
  	System.out.println("====================== login_with_token =====================");
  	String username = request.getParameter("username");
  	String password = request.getParameter("password");
  	
  	System.out.println(username + ", " + password);
  	
//  	User user = userMapper.selectByUserPwd(username, password);
  	Map<String, Object> loginMessage = tokenOperationService.login(username, password);
  	
  	if(loginMessage.get("token") != null) {
  		System.out.println("Login Success!");
  		System.out.println(loginMessage.get("token"));
  		System.out.println(loginMessage.get("user_info"));
  	}
  	else{
  		System.out.println(loginMessage.get("code"));
  		System.out.println(loginMessage.get("msg"));
  	}
  	
  	return loginMessage;
  }
  
  @RequestMapping(value="/update_token",method= RequestMethod.POST)
  public Map<String, TokenEntity> updateToken(HttpServletRequest request, HttpServletResponse response) {
  	System.out.println("====================== update_token =====================");
  	String username = request.getParameter("username");
  	
  	System.out.println(username);
  	
  	Map<String, TokenEntity> token = tokenOperationService.updateToken(username);
  	
  	if(token != null) {
  		System.out.println("Login Success!");
  		System.out.println(token.get("token"));
  	}
  	else{
  		System.out.println("update token failed!");
  	}
  	
  	return token;
  }
    
    @RequestMapping(value="/people",method= RequestMethod.GET)
    public List<People> getAllPeople(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== people =====================");
    	List<People> people = peopleMapper.selectAll();
    	
    	if(people != null) {
    		System.out.println("getAllPeople Success!");
//    		for(People one : people) {
//    			  System.out.println(one.getName());
//    		}
    	}
    	else
    	{
    		System.out.println("getAllPeople Fail!");
    	}
    	
    	return people;
 
    }
    
    @RequestMapping(value="/single_people",method= RequestMethod.GET)
    public People getOnePeople(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== single_people =====================");
    	String username = request.getParameter("username");
    	People people = peopleMapper.selectByPrimaryKey(username);
    	System.out.println(username);
    	
    	if(people != null) {
    		System.out.println("getOnePeople Success!");
//    		System.out.println(people);
    	}
    	else
    	{
    		System.out.println("getOnePeople Fail!");
    	}
    	
    	return people;
 
    }
    
    @RequestMapping(value="/friends",method= RequestMethod.GET)
    public List<Friend> getSomeFriends(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== friends =====================");
    	int num1 = Integer.parseInt(request.getParameter("num1"));
    	int num2 = Integer.parseInt(request.getParameter("num2"));
//    	int num1 = 0, num2 = 10;
    	List<Friend> friends = friendMapper.selectByNums(num1, num2);
    	
    	if(friends != null) {
    		System.out.println("getSomeFriends Success!" + String.valueOf(num1) + ", " + String.valueOf(num2));
//    		System.out.println(friends);
    		for(Friend f : friends) {
//    			System.out.println(f.getContent());
//    			System.out.println(f.getFriendImgs());
//    			System.out.println(f.getName());
//    			System.out.println(f.getId());
    		}
    	}
    	else
    	{
    		System.out.println("getSomeFriends Fail!");
    	}
    	
    	return friends;
 
    }
    
    @RequestMapping(value="/me",method= RequestMethod.GET)
    public User getMyInfo(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== me =====================");
    	String username = request.getParameter("username");
    	System.out.println(username);
    	User user = userMapper.selectByName(username);
    	
    	if(user != null) {
    		System.out.println("Me Success!");
    		System.out.println(user.getName());
    		System.out.println(user.getPwd());
    	}
    	else
    	{
    		System.out.println("Me Fail!");
    	}
    	return user;
    }
    
    @RequestMapping(value="/change_nickname",method= RequestMethod.POST)
    public User setMyInfo(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== change_nickname =====================");
    	String oldName = request.getParameter("oldName");
    	String newName = request.getParameter("newName");
    	
    	System.out.println(oldName + " -> " + newName);
    	
    	User user = userMapper.selectByNickname(oldName);
    	System.out.println(user);
    	
    	if(user != null)
    	{
    		user.setNickname(newName);
    		userMapper.updateByPrimaryKey(user);
    		System.out.println(user);
    	}
    	return user;
    }
    
    @RequestMapping(value="/weixin",method= RequestMethod.GET)
    public List<Record> getWeixin(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== getWeixin =====================");
    	String userName = request.getParameter("userName");
    	List<Record> records = dataService.getAllRecords(userName);
    	if(dataService == null) {
	  		System.out.println("dataService == null ....");
	  	}
    	
    	if(records != null){
    		System.out.println("getWeixin Success!");
    	}
    	else {
    		System.out.println("getWeixin Fail...");
    	}
    	
    	System.out.println("====================== getWeixin over=====================");
    	return records;
    }
    
    @RequestMapping(value="/select_records",method= RequestMethod.GET)
    public List<Record> selectRecords(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== select_records =====================");
    	String userName = request.getParameter("userName");
    	String toName = request.getParameter("toName");
    	int num1 = Integer.parseInt(request.getParameter("num1"));
    	int num2 = Integer.parseInt(request.getParameter("num2"));
    	System.out.println(userName + ", " + toName + ", " + num1 + ", " + num2);

    	List<Record> records = dataService.getSomeRecords(userName, toName, num1, num2);
    	if(dataService == null) {
	  		System.out.println("dataService == null ....");
	  	}
    	
    	if(records != null){
    		System.out.println("getWeixin Success!");
    		for(Record record : records) {
    			System.out.println(record.getName() + " -> " + record.getWords());
    		}	
    	}
    	else {
    		System.out.println("getWeixin Fail...");
    	}
    	
    	System.out.println("====================== select_records over=====================");
    	return records;
    }
    
    @RequestMapping(value="/add_new_record",method= RequestMethod.POST)
    public boolean addNewRecord(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== add_new_record =====================");
    	String message = request.getParameter("newRecord");
    	String toName = request.getParameter("toName");
    	String name = request.getParameter("name");
    	String chatImg = request.getParameter("chatimg");
    	
    	boolean flag = dataService.addNewRecord(message, toName, name, chatImg);
    	System.out.println(flag);
    	System.out.println("====================== add_new_record over =====================");
    	return flag;
    	
    }
    
    @RequestMapping(value="/add_new_img_record",method= RequestMethod.POST)
    public String addNewImageRecord(@RequestParam(value = "file", required = false)MultipartFile file, 
    		@RequestParam(value = "fileName", required = false)String fileName, HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== add_new_img_record =====================");
    	System.out.println(file);
    	System.out.println(file.getName());
    	System.out.println(fileName);
    	
    	//保存图片到服务器
        String path = localPath + "static\\chatimg\\";
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String fileNameA = "";
        if (file.isEmpty()) {
            return "fail";
        }

        fileNameA = fileName;
        try {
  	        OutputStream out = new FileOutputStream(new File(path + fileNameA));
  	        out.write(file.getBytes());
  	        out.flush();
  	        out.close();
  	        
			String message = request.getParameter("newRecord");
			String toName = request.getParameter("toName");
			String name = request.getParameter("name");
			String chatImg = request.getParameter("chatimg");
			
			boolean flag = dataService.addNewRecord(message, toName, name, chatImg);
			System.out.println(flag);
			System.out.println("====================== add_new_img_record over =====================");
			
			if(flag) {
				return fileNameA;
			}
			else {
				return "fail";
			}
        } 
        catch (IOException e) {
              e.printStackTrace();
              return "fail";
            }

    }
    
//    @RequestMapping(value="/get_chatimg",method= RequestMethod.POST)
//    public String getChatImage(HttpServletRequest request, HttpServletResponse response) {
//    	System.out.println("====================== add_new_img_record =====================");
//    	String chatImg = request.getParameter("chatimg");
//    	System.out.println(chatImg);
//    	
//    	//保存图片到服务器
//        String path = "C:\\Users\\18217\\Desktop\\javaPro\\demo-0731\\src\\main\\resources\\static\\chatimg\\";
//        File filePath = new File(path + chatImg);
//        
//        return "";
//    }
    	
    @RequestMapping(value="/get_head_img",method= RequestMethod.GET)
    public String getHeadImg(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== get_head_img =====================");
    	String name = request.getParameter("username");
    	System.out.println(name);
    	
    	User user = userMapper.selectByName(name);
    	
    	if(user == null) {
    		System.out.println("get_head_img fail, select error");
    		return "error";
    	}
    	else {
    		return user.getImg();
    	}
    
    
    }
    
    @RequestMapping(value="/get_head_img_base64",method= RequestMethod.GET)
    public void getHeadImgBase64(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("====================== get_head_img =====================");
    	String headerImgPath = localPath + "static\\img\\";
    	String imgName = request.getParameter("imgName");
    	System.out.println(request.getRequestURL());
    	System.out.println(imgName);
    	File file = null;
    	FileInputStream fis = null;
    	byte[] data = null;
    	
    	try {
    		file = new File(headerImgPath + imgName);
    		if(!file.exists()) {
    			System.out.println("file not exist");
    		}
    		System.out.println("file exist");
    		fis = new FileInputStream(file);
    		data = new byte[fis.available()];
    		fis.read(data);
            fis.close();
			//对字节数组Base64编码
			BASE64Encoder encoder = new BASE64Encoder();
			//返回Base64编码过的字节数组字符串
			
			String [] d = imgName.split("\\.");//将字符串分成数组
			String dataPrix = ""; //base64格式前头
		    String data2 = "";//实体部分数据
			if(d != null && d.length == 2){
	            dataPrix = d[0];
	            data2 = d[1];
	        }else {
	            System.out.println("失败，数据不合法");
	            System.out.println(d.length);
	        }
			String suffix = "";
			if("jpg".equalsIgnoreCase(data2)){
		        suffix = "data:image/jpeg;";
		    }else if("ico;".equalsIgnoreCase(data2)){
		        //data:image/x-icon;base64,base64编码的icon图片数据
		        suffix = "data:image/x-icon;";
		    }else if("data:image/gif;".equalsIgnoreCase(data2)){
		        //data:image/gif;base64,base64编码的gif图片数据
		        suffix = "gif";
		    }else if("png".equalsIgnoreCase(data2)){
		        //data:image/png;base64,base64编码的png图片数据
		        suffix = "data:image/png;";
		    }else {
		        System.out.println("上传图片格式不合法");
		    }
			
			response.setContentType("text/html;charset=utf-8");
	        PrintWriter out= null;
	        out = response.getWriter();
	        out.print(suffix + ";base64," + encoder.encode(data));
	        out.flush();
	        out.close();
	        
    	}
    	catch (final Exception e) {
    		e.printStackTrace();
    		System.err.println(e.getMessage());
    	}
    	finally {
    		if(fis != null) {
    			try {
    				fis.close();
    			}catch(final IOException e) {
    				fis = null;
    			}
    		}
    		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    	}
    }

	@RequestMapping(value="/upload_head_img",method= RequestMethod.POST)
	public String uploadImage(HttpServletRequest request, HttpServletResponse response){
		String imgPath = localPath + "static\\img\\";
		
		String base64Data = request.getParameter("content");
		String fileName = request.getParameter("fileName");
		String userName = request.getParameter("name");
		System.out.println("==上传图片==");
		System.out.println("==接收到的数据=="+base64Data);
		System.out.println("==接收到的文件名=="+fileName);
		System.out.println("==用户名=="+userName);
	
	
	    String dataPrix = ""; //base64格式前头
	    String data = "";//实体部分数据
	    if(base64Data==null||"".equals(base64Data)){
	        System.out.println("上传失败，上传图片数据为空");
	    }else {
	        String [] d = base64Data.split("base64,");//将字符串分成数组
	        if(d != null && d.length == 2){
	            dataPrix = d[0];
	            data = d[1];
	        }else {
	            System.out.println("上传失败，数据不合法");
	        }
	    }
	    String suffix = "";//图片后缀，用以识别哪种格式数据
	    //data:image/jpeg;base64,base64编码的jpeg图片数据
	    if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){
	        suffix = ".jpg";
	    }else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){
	        //data:image/x-icon;base64,base64编码的icon图片数据
	        suffix = ".ico";
	    }else if("data:image/gif;".equalsIgnoreCase(dataPrix)){
	        //data:image/gif;base64,base64编码的gif图片数据
	        suffix = ".gif";
	    }else if("data:image/png;".equalsIgnoreCase(dataPrix)){
	        //data:image/png;base64,base64编码的png图片数据
	        suffix = ".png";
	    }else {
	        System.out.println("上传图片格式不合法");
	    }

	    long time = System.currentTimeMillis();
	    String tempFileName = Long.toString(time) + suffix;
	    String imgFilePath = imgPath + tempFileName;//新生成的图片
	    File newFile = new File(imgFilePath);
	    BASE64Decoder decoder = new BASE64Decoder();
	    try {
	        //Base64解码
	        byte[] b = decoder.decodeBuffer(data);
	        for(int i=0;i<b.length;++i) {
	            if(b[i]<0) {
	                //调整异常数据
	                b[i]+=256;
	            }
	        }
	        OutputStream out = new FileOutputStream(newFile);
	        out.write(b);
	        out.flush();
	        out.close();
	        
	        User user = userMapper.selectByName(userName);
	        System.out.println("old: ");
	    	System.out.println(user.getImg());
	    	
	    	if(user != null)
	    	{
	    		user.setImg(tempFileName);
	    		userMapper.updateByPrimaryKey(user);
	    		System.out.println("new: ");
	    		System.out.println(user.getImg());
	    		
	    		return tempFileName;
	    	}
	    	else {
	    		return "fail";
	    	}
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("上传图片失败");
	        return "fail";
	    }
	
	}
	
	@RequestMapping(value="/update_head_img",method= RequestMethod.POST)
    public String updateHeadImage(@RequestParam(value = "file", required = false)MultipartFile file, 
    		@RequestParam(value = "fileName", required = false)String fileName, HttpServletResponse response) {
    	System.out.println("====================== update_head_img =====================");
    	System.out.println(file);
    	System.out.println(file.getName());
    	System.out.println(fileName);
    	
    	//保存图片到服务器
        String path = localPath + "static\\headimg\\";
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String fileNameA = "";
        if (file.isEmpty()) {
            return "fail";
        }

        fileNameA = fileName;
        try {
  	        OutputStream out = new FileOutputStream(new File(path + fileNameA));
  	        out.write(file.getBytes());
  	        out.flush();
  	        out.close();
            } catch (IOException e) {
              e.printStackTrace();
              return "fail";
            }
        return fileNameA;
    }

	@RequestMapping("/addImage")
	@ResponseBody
	public String addImage(@RequestParam(name = "image_data", required = false) MultipartFile file) {
	    //文件上传
		System.out.println("addImage");
	    if (!file.isEmpty()) {
	        try {
	            //图片命名
	            String newCompanyImageName = "newPIC.jpg";
	            String newCompanyImagepath = "C:\\Users\\18217\\Desktop\\javaPro\\demo-1\\src\\main\\resources\\static\\"+newCompanyImageName;
	            File newFile = new File(newCompanyImagepath);
	            if (!newFile.exists()) {
	                newFile.createNewFile();
	            }
//	            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
//	            out.write(file.getBytes());
//	            out.flush();
//	            out.close();
	            OutputStream out = new FileOutputStream(newFile);
		        out.write(file.getBytes());
		        out.flush();
		        out.close();
		        
	            System.out.println( "图片上传成功！");
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	            System.out.println( "图片上传失败！");
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println( "图片上传失败！");
	        }
	    }
	    return "图片上传失败！";
	}
}

