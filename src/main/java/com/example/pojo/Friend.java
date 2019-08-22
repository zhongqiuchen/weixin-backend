package com.example.pojo;

public class Friend {
    private Integer id;

    private String name;

    private String content;
    
    private String friend_imgs;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
    
    public String getFriendImgs() {
        return friend_imgs;
    }

    public void setFriendImgs(String friendImgs) {
        this.friend_imgs = friendImgs == null ? null : friendImgs.trim();
    }

}