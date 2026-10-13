package com.novatube.common.app.views;

import com.novatube.common.app.models.data.VideoGroup;

public interface ChannelUploadsView {
    void update(VideoGroup videoGroup);
    void showProgressBar(boolean show);
    void clear();
}
