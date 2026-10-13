package com.novatube.common.app.views;

import com.novatube.common.app.models.playback.manager.PlayerManager;
import com.novatube.common.app.models.playback.listener.PlayerEventListener;

public interface PlaybackView extends PlayerManager {
    void showProgressBar(boolean show);
}
