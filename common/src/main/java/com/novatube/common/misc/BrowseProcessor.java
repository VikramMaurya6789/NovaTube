package com.novatube.common.misc;

import com.novatube.common.app.models.data.Video;
import com.novatube.common.app.models.data.VideoGroup;

public interface BrowseProcessor {
    interface OnItemReady {
        void onItemReady(Video video);
    }
    void process(VideoGroup videoGroup);
    void dispose();
}
