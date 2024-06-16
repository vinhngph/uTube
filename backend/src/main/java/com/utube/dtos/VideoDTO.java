package com.utube.dtos;

import com.utube.utils.Config;

public class VideoDTO {
    private String videoId;
    private String videoTitle;
    private String videoDescription;
    private String videoDate;
    private boolean videoStatus;
    private String videoThumbnail;
    private long videoLike;
    private long videoDislike;
    private long videoViews;
    private String videoChannelName;
    private int videoChannelId;

    public VideoDTO(String videoId, String videoTitle, String videoDescription, String videoDate, boolean videoStatus,
            String videoThumbnail, long videoLike, long videoDislike, long videoViews, String videoChannelName,
            int videoChannelId) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoStatus = videoStatus;
        this.videoThumbnail = Config.getProperty("SERVER_URL") + "/api/video/thumbnail?id=" + videoThumbnail;
        this.videoLike = videoLike;
        this.videoDislike = videoDislike;
        this.videoViews = videoViews;
        this.videoChannelName = videoChannelName;
        this.videoChannelId = videoChannelId;
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

    public long getVideoLike() {
        return videoLike;
    }

    public void setVideoLike(long videoLike) {
        this.videoLike = videoLike;
    }

    public long getVideoDislike() {
        return videoDislike;
    }

    public void setVideoDislike(long videoDislike) {
        this.videoDislike = videoDislike;
    }

    public long getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(long videoViews) {
        this.videoViews = videoViews;
    }

    public String getVideoChannelName() {
        return videoChannelName;
    }

    public void setVideoChannelName(String videoChannelName) {
        this.videoChannelName = videoChannelName;
    }

    public int getVideoChannelId() {
        return videoChannelId;
    }

    public void setVideoChannelId(int videoChannelId) {
        this.videoChannelId = videoChannelId;
    }
}
