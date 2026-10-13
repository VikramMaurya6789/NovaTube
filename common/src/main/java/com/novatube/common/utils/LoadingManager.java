package com.novatube.common.utils;

import android.app.Activity;
import android.content.Context;
import com.novatube.common.app.presenters.BrowsePresenter;
import com.novatube.common.app.presenters.ChannelPresenter;
import com.novatube.common.app.presenters.ChannelUploadsPresenter;
import com.novatube.common.app.presenters.PlaybackPresenter;
import com.novatube.common.app.presenters.SearchPresenter;
import com.novatube.common.app.views.BrowseView;
import com.novatube.common.app.views.ChannelUploadsView;
import com.novatube.common.app.views.ChannelView;
import com.novatube.common.app.views.PlaybackView;
import com.novatube.common.app.views.SearchView;
import com.novatube.common.app.views.ViewManager;

public class LoadingManager {
    public static void showLoading(Context context, boolean show, Class<?> view) {
        Class<?> topView = ViewManager.instance(context).getTopView();
        if (topView == view) {
            showLoading(context, show);
        }
    }

    public static void showLoading(Context context, boolean show) {
        if (!(context instanceof Activity)) {
            return;
        }

        Class<?> topView = ViewManager.instance(context).getView(((Activity) context).getClass());

        if (topView == BrowseView.class) {
            BrowseView browseView = BrowsePresenter.instance(context).getView();
            if (browseView != null) {
                browseView.showProgressBar(show);
            }
        } else if (topView == SearchView.class) {
            SearchView searchView = SearchPresenter.instance(context).getView();
            if (searchView != null) {
                searchView.showProgressBar(show);
            }
        } else if (topView == ChannelView.class) {
            ChannelView channelView = ChannelPresenter.instance(context).getView();
            if (channelView != null) {
                channelView.showProgressBar(show);
            }
        } else if (topView == ChannelUploadsView.class) {
            ChannelUploadsView uploadsView = ChannelUploadsPresenter.instance(context).getView();
            if (uploadsView != null) {
                uploadsView.showProgressBar(show);
            }
        } else if (topView == PlaybackView.class) {
            PlaybackView playbackView = PlaybackPresenter.instance(context).getView();
            if (playbackView != null) {
                playbackView.showProgressBar(show);
            }
        }
    }
}
