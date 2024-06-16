package com.utube.dtos;

public class VideoInformationDTO {
    private String videoId;
    private String videoTitle;
    private String videoDescription;
    private String videoDate;
    private boolean videoStatus;
    private int videoOwner;

    public VideoInformationDTO(String videoId, String videoTitle, String videoDescription, String videoDate,
            boolean videoStatus, int videoOwner) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoStatus = videoStatus;
        this.videoOwner = videoOwner;
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

    public int getVideoOwner() {
        return videoOwner;
    }

    public void setVideoOwner(int videoOwner) {
        this.videoOwner = videoOwner;
    }
}
