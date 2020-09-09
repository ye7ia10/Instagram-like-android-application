package com.yehiaelsayed.myins.Models;

public class User {
    private String fullname="";
    private String username="";
    private String bio="";
    private String image="" ;
    private String uid="";
    private String email="";



    public User() {
    }

    public User(String fullname, String username, String bio, String image, String uid, String email) {
        this.fullname = fullname;
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.uid = uid;
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
