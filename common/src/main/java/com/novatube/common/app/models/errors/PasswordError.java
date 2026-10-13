package com.novatube.common.app.models.errors;

import android.content.Context;
import com.novatube.common.R;
import com.novatube.common.app.presenters.settings.AccountSettingsPresenter;

public class PasswordError implements ErrorFragmentData {
    private final Context mContext;

    public PasswordError(Context context) {
        mContext = context;
    }

    @Override
    public void onAction() {
        AccountSettingsPresenter.instance(mContext).showCheckPasswordDialog();
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public String getActionText() {
        return mContext.getString(R.string.enter_account_password);
    }
}
