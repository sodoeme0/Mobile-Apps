package edu.uncc.hw07;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Forum implements Serializable {
    String title, desc, date, user_id, forum_id, createdBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    int likes;
    Map<String, Integer> userLikes = new HashMap<>();

    public Map<String, Integer> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(Map<String, Integer> userLikes) {
        this.userLikes = userLikes;
    }

    public Forum(){}

    @Override
    public String toString() {
        return "Forum{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", user_id='" + user_id + '\'' +
                ", likes=" + likes +
                '}';
    }

    public Forum(String title , String desc, String user_id) {
        this.title = title;
        this.desc = desc;
        this.date = String.valueOf(new Date()) ;
        this.user_id = user_id;
        this.likes = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
