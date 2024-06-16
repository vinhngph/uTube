package com.utube.dtos;

public class VideoInteractionDTO {
    private String video_id;
    private long like;
    private long dislike;
    private long view;

    public VideoInteractionDTO(String video_id, long like, long dislike, long view) {
        this.video_id = video_id;
        this.like = like;
        this.dislike = dislike;
        this.view = view;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public long getDislike() {
        return dislike;
    }

    public void setDislike(long dislike) {
        this.dislike = dislike;
    }

    public long getView() {
        return view;
    }

    public void setView(long view) {
        this.view = view;
    }

}
