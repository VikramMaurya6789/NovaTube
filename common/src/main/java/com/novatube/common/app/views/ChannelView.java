package com.novatube.common.app.views;

import com.novatube.common.app.models.data.VideoGroup;

public interface ChannelView {
    void update(VideoGroup videoGroup);
    void setPosition(int index);
    void showProgressBar(boolean show);
    void clear();
}
