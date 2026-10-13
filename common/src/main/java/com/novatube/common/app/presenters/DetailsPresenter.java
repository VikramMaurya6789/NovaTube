package com.novatube.common.app.presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import com.novatube.common.app.models.data.Video;
import com.novatube.common.app.presenters.base.BasePresenter;
import com.novatube.common.app.views.DetailsView;
import com.novatube.common.app.views.ViewManager;

public class DetailsPresenter extends BasePresenter<DetailsView> {
    @SuppressLint("StaticFieldLeak")
    private static DetailsPresenter sInstance;
    private Video mVideo;

    private DetailsPresenter(Context context) {
        super(context);
    }

    public static DetailsPresenter instance(Context context) {
        if (sInstance == null) {
            sInstance = new DetailsPresenter(context);
        }

        sInstance.setContext(context);

        return sInstance;
    }

    @Override
    public void onViewInitialized() {
        getView().openVideo(mVideo);
    }

    public void openVideo(Video item) {
        mVideo = item;
        getViewManager().startView(DetailsView.class);
    }
}
