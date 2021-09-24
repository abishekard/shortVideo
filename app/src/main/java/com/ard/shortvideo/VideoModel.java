package com.ard.shortvideo;

public class VideoModel {

    private String desc;
    private String videoUrl;
    private String thumbUrl;
    private String gifUrl;

    public VideoModel(String desc, String videoUrl, String thumbUrl, String gifUrl) {
        this.desc = desc;
        this.videoUrl = videoUrl;
        this.thumbUrl = thumbUrl;
        this.gifUrl = gifUrl;
    }

    public String getDesc() {
        return desc;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getGifUrl() {
        return gifUrl;
    }
}
