package com.yehiaelsayed.myins.Models;

public class Comment {
    private String user;
    private String comment;

    public Comment(String user, String comment) {
        this.user = user;
        this.comment = comment;
    }

    public Comment() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
