package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;

import com.example.dao.TokenEntityMapper;
import com.example.demo.service.ITokenOperationService;
import com.example.pojo.*;

@Component
public class LoginInterceptor implements HandlerInterceptor{
	
	@Resource
	private TokenEntityMapper tokenEntityMapper;
	
	@Resource
	private ITokenOperationService tokenOperationService;
 
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    	System.out.println("=== postHandle ===");
    }
 
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    	System.out.println("=== afterCompletion ===");
    }
    
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
    	System.out.println("===================== preHandle =========================");
    	System.out.println("------------------------------------执行时间 1" + new Date().getTime());
    	//此处为不需要登录的接口放行
        if (httpServletRequest.getRequestURI().contains("/login_with_token") || httpServletRequest.getRequestURI().contains("/register") || httpServletRequest.getRequestURI().contains("/error") || httpServletRequest.getRequestURI().contains("/static")) {
        	System.out.println("不需要登录");
        	return true;
        }
        else {
        	System.out.println("需要登录");
        	System.out.println(httpServletRequest.getRequestURI());
        	System.out.println(httpServletRequest.toString());
        }
        //权限路径拦截
        //PrintWriter resultWriter = httpServletResponse.getOutputStream();
        // TODO: 有时候用PrintWriter 回报 getWriter() has already been called for this response
        //换成ServletOutputStream就OK了
        System.out.println(httpServletRequest.getContentType());
        httpServletResponse.setContentType(httpServletRequest.getContentType());
//        httpServletResponse.setContentType("text/html;charset=utf-8");
        
//        ServletOutputStream resultWriter = httpServletResponse.getOutputStream();
//        PrintWriter out = httpServletResponse.getWriter();
        final String headerToken=httpServletRequest.getHeader("token");
        String userName=httpServletRequest.getParameter("username");
        System.out.println("headerToken: " + headerToken);
        JSONObject result = new JSONObject();
        
        //判断请求信息
        if(null==headerToken||headerToken.trim().equals("")){
        	result.put("code", 402);
        	result.put("msg", "your_token_is_null");
        	
        	System.out.println("你没有token,需要登录");
//        	ServletOutputStream resultWriter = httpServletResponse.getOutputStream();
//            resultWriter.write(result.toString().getBytes());
        	PrintWriter resultWriter = httpServletResponse.getWriter();
            resultWriter.print(result);
            resultWriter.flush();
            resultWriter.close();
            return false;
        }
        //解析Token信息
        try {
        	System.out.println("解析Token信息");
            Claims claims = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(headerToken).getBody();
            String tokenUserName=(String)claims.get("userName");
            
            //根据客户Token查找数据库Token
            TokenEntity myToken= tokenEntityMapper.selectByName(tokenUserName);
 
            //数据库没有Token记录
            if(null==myToken) {
            	result.put("code", 403);
            	result.put("msg", "no_token");
            	System.out.println("我没有你的token？,需要登录");
            	PrintWriter resultWriter = httpServletResponse.getWriter();
                resultWriter.print(result);
                resultWriter.flush();
                resultWriter.close();
                return false;
            }
            //数据库Token与客户Token比较
            if( !headerToken.equals(myToken.getToken()) ){
            	result.put("code", 404);
            	result.put("msg", "wrong_token");
            	
            	System.out.println("你的token修改过？,需要登录");
            	System.out.println("前端的token：");
            	System.out.println(headerToken);
            	System.out.println("数据库的token：");
            	System.out.println(myToken.getToken());
            	System.out.println("------------------------------------执行时间 2" + new Date().getTime());
            	
            	PrintWriter resultWriter = httpServletResponse.getWriter();
                resultWriter.print(result);
                resultWriter.flush();
                resultWriter.close();
                return false;
            }
            //判断Token过期
            Date tokenDate= claims.getExpiration();
            int overTime=(int)(tokenDate.getTime() - new Date().getTime())/1000;
            System.out.println("overtime: " + overTime);
            if(overTime<0){
            	result.put("code", 405);
            	result.put("msg", "token_out_of_date");
            	System.out.println("你的token过期了？,需要登录");
            	PrintWriter resultWriter = httpServletResponse.getWriter();
                resultWriter.print(result);
                resultWriter.flush();
                resultWriter.close();
                return false;
            }
            else {
            	if(overTime<3000) {
            		//token快要过期了，更新token
            		System.out.println("你的token快要过期了，这就更新一下");
            		Map<String, TokenEntity> loginMessage = tokenOperationService.updateToken(tokenUserName);
            		if(loginMessage.get("token") != null) {
            	  		System.out.println("update Success!");
            	  		System.out.println(loginMessage.get("token"));
            	  		System.out.println(tokenUserName);
            	  	}
            	  	else{
            	  		System.out.println(loginMessage.get("code"));
            	  		System.out.println(loginMessage.get("msg"));
            	  	}
            		TokenEntity newToken = loginMessage.get("token");
            		result.put("code", 406);
                	result.put("msg", "your_token_is_about_to_expire");
                	result.put("new_token", newToken.getToken());
                	
            		
            		System.out.println("更新后的token：" + newToken.getToken());
            		
            		PrintWriter resultWriter = httpServletResponse.getWriter();
                    resultWriter.print(result);
                    resultWriter.flush();
//                    resultWriter.close();
//                    resultWriter = null;
                    return false;
            	}
            }
 
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put("code", 407);
        	result.put("msg", "token_error");
        	System.out.println("反正token不对,需要登录");
        	ServletOutputStream resultWriter = httpServletResponse.getOutputStream();
            resultWriter.write(result.toString().getBytes());
            resultWriter.flush();
            resultWriter.close();
            return false;
        }
        //最后才放行
        System.out.println("===================== over preHandle =========================");
    	return true;
    }
    
    public SecretKey generalKey(){
		System.out.println("=== generalKey ===");
        String stringKey = "7786df7fc3a34e26a61c034d5ec8245d";//本地配置文件中加密的密文7786df7fc3a34e26a61c034d5ec8245d
        byte[] encodedKey = Base64.decodeBase64(stringKey);//本地的密码解码[B@152f6e2
//        System.out.println(encodedKey);//[B@152f6e2
//        System.out.println(Base64.encodeBase64URLSafeString(encodedKey));//7786df7fc3a34e26a61c034d5ec8245d
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");// 根据给定的字节数组使用AES加密算法构造一个密钥，使用 encodedKey中的始于且包含 0 到前 leng 个字节这是当然是所有。（后面的文章中马上回推出讲解Java加密和解密的一些算法）
        return key;
    }

}
