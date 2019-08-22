package com.example.pojo;

public class TokenEntity {
    private Integer tokenId;

    private String userName;

    private Integer buildTime;

    private String token;

    public Integer getTokenId() {
        return tokenId;
    }

    public void setTokenId(Integer tokenId) {
        this.tokenId = tokenId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Integer getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Integer buildTime) {
        this.buildTime = buildTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }
}