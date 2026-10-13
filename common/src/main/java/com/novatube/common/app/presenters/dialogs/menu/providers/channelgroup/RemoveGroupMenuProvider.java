package com.novatube.common.app.presenters.dialogs.menu.providers.channelgroup;

import android.content.Context;

import androidx.annotation.NonNull;

import com.novatube.common.R;
import com.novatube.common.app.models.data.Video;
import com.novatube.common.app.presenters.AppDialogPresenter;
import com.novatube.common.app.presenters.BrowsePresenter;
import com.novatube.common.app.presenters.dialogs.menu.VideoMenuPresenter.VideoMenuCallback;
import com.novatube.common.app.presenters.dialogs.menu.providers.ContextMenuProvider;
import com.novatube.common.utils.AppDialogUtil;

public class RemoveGroupMenuProvider extends ContextMenuProvider {
    private final ChannelGroupServiceWrapper mService;

    public RemoveGroupMenuProvider(@NonNull Context context, int idx) {
        super(context, idx);
        mService = ChannelGroupServiceWrapper.instance(context);
    }

    @Override
    public int getTitleResId() {
        return R.string.unpin_group_from_sidebar;
    }

    @Override
    public void onClicked(Video item, VideoMenuCallback callback) {
        AppDialogUtil.showConfirmationDialog(getContext(), getContext().getString(R.string.unpin_group_from_sidebar), () -> {
            mService.removeChannelGroup(
                    mService.findChannelGroupById(item.channelGroupId)
            );
            BrowsePresenter.instance(getContext()).unpinItem(item);
            AppDialogPresenter.instance(getContext()).closeDialog();
        });
    }

    @Override
    public boolean isEnabled(Video item) {
        return item != null && item.channelGroupId != null;
    }

    @Override
    public int getMenuType() {
        return MENU_TYPE_SECTION;
    }
}
