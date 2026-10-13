package com.novatube.tv.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;

import com.liskovsoft.sharedutils.mylogger.Log;
import com.novatube.common.app.presenters.SearchPresenter;
import com.novatube.common.autoframerate.ModeSyncManager;
import com.novatube.common.misc.GlobalKeyTranslator;
import com.novatube.common.misc.MotherActivity;
import com.novatube.common.misc.PlayerKeyTranslator;
import com.novatube.common.prefs.GeneralData;
import com.novatube.common.utils.Utils;
import com.novatube.tv.ui.common.keyhandler.DoubleBackManager2;
import com.novatube.tv.ui.playback.PlaybackActivity;
import com.novatube.tv.ui.search.tags.SearchTagsActivity;

/**
 * This parent class contains common methods that run in every activity such as search.
 */
public abstract class LeanbackActivity extends MotherActivity {
    private static final String TAG = LeanbackActivity.class.getSimpleName();
    private UriBackgroundManager mBackgroundManager;
    private ModeSyncManager mModeSyncManager;
    private DoubleBackManager2 mDoubleBackManager;
    private GlobalKeyTranslator mGlobalKeyTranslator;
    private final Runnable sOnFinish = () -> Utils.forceFinishTheApp(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBackgroundManager = new UriBackgroundManager(this);
        mModeSyncManager = ModeSyncManager.instance();
        mDoubleBackManager = new DoubleBackManager2(this);
        mGlobalKeyTranslator = this instanceof PlaybackActivity ?
                new PlayerKeyTranslator(this) :
                new GlobalKeyTranslator(this);
        mGlobalKeyTranslator.apply();
    }

    @Override
    public boolean onSearchRequested() {
        SearchPresenter.instance(this).startSearch(null);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, event);

        KeyEvent newEvent = mGlobalKeyTranslator.translate(event);
        return super.dispatchKeyEvent(newEvent);
    }

    public UriBackgroundManager getBackgroundManager() {
        return mBackgroundManager;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBackgroundManager.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // PIP fix: While entering/exiting PIP mode only Pause/Resume is called

        mGlobalKeyTranslator.apply(); // adapt to state changes (like enter/exit from PIP mode)

        mModeSyncManager.restore(this);

        getViewManager().addTop(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBackgroundManager.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackgroundManager.onDestroy();
    }

    @Override
    public void finish() {
        // user pressed back key
        if (!getViewManager().hasParentView(this)) {
            switch (getGeneralData().getAppExitShortcut()) {
                case GeneralData.EXIT_DOUBLE_BACK:
                    mDoubleBackManager.enableDoubleBackExit(this::finishTheApp);
                    break;
                case GeneralData.EXIT_SINGLE_BACK:
                    finishTheApp();
                    break;
            }
        } else if (this instanceof PlaybackActivity) {
            switch (getGeneralData().getPlayerExitShortcut()) {
                case GeneralData.EXIT_DOUBLE_BACK:
                    mDoubleBackManager.enableDoubleBackExit(this::finishReally);
                    break;
                case GeneralData.EXIT_SINGLE_BACK:
                    finishReally();
                    break;
            }
        } else if (this instanceof SearchTagsActivity) {
            switch (getGeneralData().getSearchExitShortcut()) {
                case GeneralData.EXIT_DOUBLE_BACK:
                    mDoubleBackManager.enableDoubleBackExit(this::finishReally);
                    break;
                case GeneralData.EXIT_SINGLE_BACK:
                    finishReally();
                    break;
            }
        } else {
            finishReally();
        }
    }

    @Override
    public void finishReally() {
        // Mandatory line. Fix un-proper view order (especially for playback view).
        getViewManager().startParentView(this);
        super.finishReally();
    }

    private void finishTheApp() {
        getViewManager().addOnFinish(sOnFinish);

        Utils.properlyFinishTheApp(this);
    }
}
