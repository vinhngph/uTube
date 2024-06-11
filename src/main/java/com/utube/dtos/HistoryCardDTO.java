package com.utube.dtos;

public class HistoryCardDTO {
    private String videoId;
    private String videoTitle;
    private String videoDescription;
    private String videoDate;
    private boolean videoStatus;
    private String videoThumbnail;
    private String trackDate;
    private String trackTime;

    public HistoryCardDTO(String videoId, String videoTitle, String videoDescription, String videoDate,
            boolean videoStatus, String videoThumbnail, String trackDate, String trackTime) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoStatus = videoStatus;
        this.videoThumbnail = videoThumbnail;
        this.trackDate = trackDate;
        this.trackTime = trackTime;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoDate() {
        return videoDate;
    }

    public void setVideoDate(String videoDate) {
        this.videoDate = videoDate;
    }

    public boolean isVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(boolean videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
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
