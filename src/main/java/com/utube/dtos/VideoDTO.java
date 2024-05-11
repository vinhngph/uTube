package com.utube.dtos;

import java.sql.Timestamp;

public class VideoDTO {
    private String videoId;
    private String videoTitle;
    private String videoDescription;
    private Timestamp videoDate;

    public VideoDTO(String videoId, String videoTitle, String videoDescription, Timestamp videoDate) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public Timestamp getVideoDate() {
        return videoDate;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public void setVideoDate(Timestamp videoDate) {
        this.videoDate = videoDate;
    }
}
