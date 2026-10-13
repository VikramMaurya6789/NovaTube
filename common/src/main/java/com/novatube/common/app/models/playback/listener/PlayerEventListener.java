package com.novatube.common.app.models.playback.listener;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItemMetadata;
import com.novatube.common.app.models.data.Video;
import com.novatube.common.app.models.playback.manager.PlayerManager;
import com.novatube.common.misc.TickleManager.TickleListener;

public interface PlayerEventListener extends PlayerUiEventListener, PlayerEngineEventListener, ViewEventListener, TickleListener {
    void onNewVideo(Video item);
    void onMetadata(MediaItemMetadata metadata);
    /**
     * Called after creation of {@link PlayerManager}
     */
    void onInit();
    void onFinish();
}
