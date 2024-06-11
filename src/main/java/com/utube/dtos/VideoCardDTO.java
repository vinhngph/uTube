package com.utube.dtos;

import com.utube.utils.Config;

public class VideoCardDTO {
    private String videoId;
    private String videoTitle;
    private String videoDescription;
    private String videoDate;
    private boolean videoStatus;
    private String videoThumbnail;
    private int videoOwner;

    public VideoCardDTO(String videoId, String videoTitle, String videoDescription, String videoDate, boolean videoStatus,
            int videoOwner) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoStatus = videoStatus;
        this.videoOwner = videoOwner;
    }

    public VideoCardDTO(String videoId, String videoTitle, String videoDescription, String videoDate, boolean videoStatus,
            String videoThumbnail, int videoOwner) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoStatus = videoStatus;
        this.videoThumbnail = Config.getProperty("SERVER_URL") + "/api/video/thumbnail?id=" + videoThumbnail;
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

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public int getVideoOwner() {
        return videoOwner;
    }

    public void setVideoOwner(int videoOwner) {
        this.videoOwner = videoOwner;
    }
}
