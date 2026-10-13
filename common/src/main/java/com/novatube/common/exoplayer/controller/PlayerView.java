package com.novatube.common.exoplayer.controller;

import com.novatube.common.app.models.data.Video;

public interface PlayerView {
    void setQualityInfo(String info);
    void setVideo(Video video);
}
