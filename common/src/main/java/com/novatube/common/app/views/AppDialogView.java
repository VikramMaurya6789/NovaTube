package com.novatube.common.app.views;

import com.novatube.common.app.models.playback.ui.OptionCategory;

import java.util.List;

public interface AppDialogView {
    void show(List<OptionCategory> categories, CharSequence title, boolean isExpandable, boolean isTransparent, boolean isOverlay, int id);
    void finish();
    void goBack();
    void clearBackstack();
    boolean canGoBack();
    boolean isShown();
    boolean isTransparent();
    boolean isOverlay();
    boolean isPaused();
    int getViewId();
}
