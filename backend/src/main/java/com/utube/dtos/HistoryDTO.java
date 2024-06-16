package com.utube.dtos;

public class HistoryDTO {
    private int userId;
    private String videoId;
    private String trackDate;
    private String trackTime;

    public HistoryDTO(int userId, String videoId, String trackDate, String trackTime) {
        this.userId = userId;
        this.videoId = videoId;
        this.trackDate = trackDate;
        this.trackTime = trackTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTrackDate() {
        return trackDate;
    }

    public void setTrackDate(String trackDate) {
        this.trackDate = trackDate;
    }

    public String getTrackTime() {
        return trackTime;
    }

    public void setTrackTime(String trackTime) {
        this.trackTime = trackTime;
    }

}
