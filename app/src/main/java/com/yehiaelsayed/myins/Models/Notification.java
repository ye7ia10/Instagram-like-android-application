package com.yehiaelsayed.myins.Models;

public class Notification {
    private String userId;
    private String  message;
    private String postID;
    private boolean seen;
    private String notificationId;
    public Notification() {
    }

    public Notification(String userId, String message, String postID, boolean seen, String notificationId) {
        this.userId = userId;
        this.message = message;
        this.postID = postID;
        this.seen = seen;
        this.notificationId = notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getNotificationId() {
        return notificationId;
    }


}
