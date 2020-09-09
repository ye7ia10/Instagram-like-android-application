package com.yehiaelsayed.myins.Models;

public class post {

    private String postCaption;
    private String postId;
    private String postUrl;
    private String postUser;

    public post(String postCaption, String postId, String postUrl, String postUser) {
        this.postCaption = postCaption;
        this.postId = postId;
        this.postUrl = postUrl;
        this.postUser = postUser;
    }

    public post(){}

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }
}
