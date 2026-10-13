package com.novatube.tv.ui.browse.interfaces;

import com.novatube.common.app.models.data.Video;
import com.novatube.common.app.models.data.VideoGroup;

public interface VideoSection extends Section {
    void update(VideoGroup group);
    int getPosition();
    void setPosition(int index);
    void selectItem(Video item);
}
