package com.example.demo.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Locale;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;

import com.example.pojo.*;
import com.example.demo.service.*;
import com.example.dao.*;

@Service("tokenOperationService")
public class TokenOperationService implements ITokenOperationService{
	
	@Resource
	private TokenEntityMapper tokenEntityMapper;
	
	@Resource
	private UserMapper userMapper;
	
	public Map<String, Object> login(String username, String password) {
		System.out.println("=== login ===");
        Map<String, Object> map = new HashMap<>();
        System.out.println(username + ", " + password);

        //根据手机号查询user对象
        User user = userMapper.selectByUserPwd(username, password);
 
        //判断用户不存在
        if (user == null) {
        	System.out.println("User does not exit!");
            map.put("code", 401);
            map.put("msg", "User does not exit!");
            return map;
        }
        else {
        	System.out.println("User exits! Let's make the token... ");
        }
 
        //根据数据库的用户信息查询Token
        return operateToKen(map, user, user.getName());
    }
	
	public Map<String, TokenEntity> updateToken(String userName) {
		System.out.println("=== updateToken ===");
        //根据数据库的用户信息查询Token
        TokenEntity token = tokenEntityMapper.selectByName(userName);
        Map<String, TokenEntity> map = new HashMap<>();
        //为生成Token准备
        String TokenStr = "";
        Date date = new Date();
        int nowTime = (int) (date.getTime() / 1000);
        //生成Token
        TokenStr = creatToken(userName, date);
        System.out.println("userName is: " + userName);
        System.out.println("TokenStr is: " + TokenStr);
        if (null == token) {
            //第一次登陆
            token = new TokenEntity();
            token.setToken(TokenStr);
            token.setBuildTime(nowTime);
            token.setUserName(userName);
            tokenEntityMapper.insert(token);
        }else{
            //登陆就更新Token信息
//            TokenStr = creatToken(userName, date);
            token.setToken(TokenStr);
            token.setBuildTime(nowTime);
            tokenEntityMapper.updateByPrimaryKey(token);
        }
        map.put("token", token);
        return map;
    }
	
	public Map<String, Object> operateToKen(Map<String, Object> map, User user, String userName) {
		System.out.println("=== operateToKen ===");
        //根据数据库的用户信息查询Token
        TokenEntity token = tokenEntityMapper.selectByName(userName);
//        System.out.println(token);
        //为生成Token准备
        String TokenStr = "";
        Date date = new Date();
        int nowTime = (int) (date.getTime() / 1000);
        //生成Token
        TokenStr = creatToken(userName, date);
//        System.out.println("TokenStr is: " + TokenStr);
        if (null == token) {
            //第一次登陆
//        	System.out.println("=== operateToKen ===  1");
            token = new TokenEntity();
            token.setToken(TokenStr);
            token.setBuildTime(nowTime);
            token.setUserName(userName);
//            token.setId(Long.valueOf(IdUtils.getPrimaryKey()));
            tokenEntityMapper.insert(token);
        }else{
            //登陆就更新Token信息
//        	System.out.println("=== operateToKen ===  2");
            TokenStr = creatToken(userName, date);
            token.setToken(TokenStr);
            token.setBuildTime(nowTime);
            tokenEntityMapper.updateByPrimaryKey(token);
        }
        user.setPwd("");//返回值中密码置空，防止泄露
        map.put("token", token);
        map.put("user_info", user);
//        System.out.println("=== operateToKen ===  4");
        return map;
    }
	
	public String creatToken(String userName, Date date) {
		System.out.println("=== creatToken ===");
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//        System.out.println("=== creatToken === 1");
        
        JwtBuilder builder = Jwts.builder()
        		.setHeaderParam("typ", "JWT") // 设置header
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(date) // 设置签发时间
                .setExpiration(new Date(date.getTime() + 1000 * 60 * 60))
                .claim("userName",userName) // 设置内容
                .setIssuer("lws")// 设置签发人
                .signWith(signatureAlgorithm, generalKey()); // 签名，需要算法和key
//        System.out.println("=== creatToken === 2");
        String jwt = builder.compact();
//        System.out.println("=== creatToken === 3");
        return jwt;
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
