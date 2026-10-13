package com.novatube.tv.launchers;

import android.os.Bundle;
import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.novatube.common.app.presenters.BrowsePresenter;
import com.novatube.common.misc.MotherActivity;

public class HomeLauncherActivity extends MotherActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BrowsePresenter.instance(this).selectSection(MediaGroup.TYPE_HOME);

        finish();
    }
}
