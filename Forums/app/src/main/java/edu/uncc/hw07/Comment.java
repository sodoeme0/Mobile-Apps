package edu.uncc.hw07;

import java.util.Date;

public class Comment {
    String name, desc, date, user_id, comment_id;

    public Comment() {
    }

    public Comment(String name, String desc,  String user_id) {
        this.name = name;
        this.desc = desc;
        this.date = String.valueOf(new Date());
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
