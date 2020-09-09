package com.yehiaelsayed.myins.Models;

public class Story {
    private String imageUri;
    private String storyId;
    private long timeStart;
    private long timeEnd;
    private String userId;

    public Story() {
    }

    public Story(String imageUri, String storyId, long timeStart, long timeEnd, String userId) {
        this.imageUri = imageUri;
        this.storyId = storyId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.userId = userId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
