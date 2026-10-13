package com.novatube.common.app.presenters.settings;

import android.content.Context;
import com.novatube.common.R;
import com.novatube.common.app.models.playback.controllers.AutoFrameRateController;
import com.novatube.common.app.models.playback.ui.OptionCategory;
import com.novatube.common.app.presenters.AppDialogPresenter;
import com.novatube.common.app.presenters.base.BasePresenter;
import com.novatube.common.prefs.PlayerData;

public class AutoFrameRateSettingsPresenter extends BasePresenter<Void> {
    private final PlayerData mPlayerData;

    public AutoFrameRateSettingsPresenter(Context context) {
        super(context);
        mPlayerData = PlayerData.instance(context);
    }

    public static AutoFrameRateSettingsPresenter instance(Context context) {
        return new AutoFrameRateSettingsPresenter(context);
    }

    public void show() {
        show(() -> {});
    }

    public void show(Runnable onFinish) {
        AppDialogPresenter settingsPresenter = AppDialogPresenter.instance(getContext());

        appendAutoFrameRateCategory(settingsPresenter);
        appendAutoFrameRatePauseCategory(settingsPresenter);
        appendAutoFrameRateModesCategory(settingsPresenter);

        settingsPresenter.showDialog(getContext().getString(R.string.auto_frame_rate), onFinish);
    }

    private void appendAutoFrameRateCategory(AppDialogPresenter settingsPresenter) {
        OptionCategory category = AutoFrameRateController.createAutoFrameRateCategory(getContext(), mPlayerData);
        settingsPresenter.appendCategory(category);
    }

    private void appendAutoFrameRatePauseCategory(AppDialogPresenter settingsPresenter) {
        OptionCategory category = AutoFrameRateController.createAutoFrameRatePauseCategory(getContext(), mPlayerData);
        settingsPresenter.appendCategory(category);
    }

    private void appendAutoFrameRateModesCategory(AppDialogPresenter settingsPresenter) {
        OptionCategory category = AutoFrameRateController.createAutoFrameRateModesCategory(getContext());
        settingsPresenter.appendCategory(category);
    }
}
