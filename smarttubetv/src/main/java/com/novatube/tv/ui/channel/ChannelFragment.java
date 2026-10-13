package com.novatube.tv.ui.channel;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liskovsoft.sharedutils.helpers.Helpers;
import com.novatube.common.app.presenters.ChannelPresenter;
import com.novatube.common.app.presenters.interfaces.VideoGroupPresenter;
import com.novatube.common.app.views.ChannelView;
import com.novatube.common.misc.CrashRestorer;
import com.novatube.common.prefs.MainUIData;
import com.novatube.tv.presenter.ChannelHeaderPresenter.ChannelHeaderCallback;
import com.novatube.tv.ui.browse.video.MultipleRowsFragment;
import com.novatube.tv.ui.mod.leanback.misc.ProgressBarManager;
import com.liskovsoft.googlecommon.common.helpers.YouTubeHelper;

public class ChannelFragment extends MultipleRowsFragment implements ChannelView {
    private static final String TAG = ChannelFragment.class.getSimpleName();
    private ChannelPresenter mChannelPresenter;
    private ProgressBarManager mProgressBarManager;
    private boolean mIsFragmentCreated;
    private CrashRestorer mCrashRestorer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null); // Real restore takes place in the presenter

        if (getContext() == null) {
            return;
        }

        mCrashRestorer = new CrashRestorer(getContext(), savedInstanceState);
        mIsFragmentCreated = true;
        mChannelPresenter = ChannelPresenter.instance(getContext());
        mChannelPresenter.setView(this);

        mProgressBarManager = new ProgressBarManager();
        if (MainUIData.instance(getContext()).isChannelSearchBarEnabled()) {
            addHeader(new ChannelHeaderCallback() {
                @Override
                public void onSearchSettingsClicked() {
                    mChannelPresenter.onSearchSettingsClicked();
                }

                @Override
                public boolean onSearchSubmit(String query) {
                    return mChannelPresenter.onSearchSubmit(query);
                }

                @Override
                public String getChannelTitle() {
                    if (mChannelPresenter.getChannel() == null) {
                        return Helpers.startsWith(mChannelPresenter.getChannelId(), "@") ? mChannelPresenter.getChannelId() : null;
                    }

                    String author = mChannelPresenter.getChannel().getAuthor();
                    String title = mChannelPresenter.getChannel().getTitle();
                    String subs = mChannelPresenter.getChannel().subscriberCount;

                    return Helpers.toString(YouTubeHelper.createInfo(Helpers.firstNonNull(author, title), subs));
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Called when the activity is paused
        mCrashRestorer.persistHeaderIndex(outState, getPosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        // Don't move to onCreateView
        mProgressBarManager.setRootView((ViewGroup) getActivity().findViewById(android.R.id.content).getRootView());

        mChannelPresenter.onViewInitialized();
        
        // Restore state after crash
        mCrashRestorer.restoreHeader((idx, video) -> {
            setPosition(idx);
        });
        mCrashRestorer.restorePlayback();
    }

    @Override
    protected VideoGroupPresenter getMainPresenter() {
        return ChannelPresenter.instance(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChannelPresenter.onViewDestroyed();
    }

    public void onFinish() {
        mChannelPresenter.onFinish();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mIsFragmentCreated) {
            mChannelPresenter.onViewResumed();
        }

        mIsFragmentCreated = false;
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            mProgressBarManager.show();
        } else {
            mProgressBarManager.hide();
        }
    }
}
