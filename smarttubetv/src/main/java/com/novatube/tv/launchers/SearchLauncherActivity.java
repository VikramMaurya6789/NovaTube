package com.novatube.tv.launchers;

import android.os.Bundle;
import com.novatube.common.app.presenters.SearchPresenter;
import com.novatube.common.misc.MotherActivity;

public class SearchLauncherActivity extends MotherActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchPresenter.instance(this).startSearch(null);

        finish();
    }
}
