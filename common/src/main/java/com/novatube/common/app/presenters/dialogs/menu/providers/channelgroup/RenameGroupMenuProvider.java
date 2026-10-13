package com.novatube.common.app.presenters.dialogs.menu.providers.channelgroup;

import android.content.Context;

import androidx.annotation.NonNull;

import com.liskovsoft.mediaserviceinterfaces.data.ItemGroup;
import com.novatube.common.R;
import com.novatube.common.app.models.data.Video;
import com.novatube.common.app.presenters.AppDialogPresenter;
import com.novatube.common.app.presenters.BrowsePresenter;
import com.novatube.common.app.presenters.dialogs.menu.VideoMenuPresenter.VideoMenuCallback;
import com.novatube.common.app.presenters.dialogs.menu.providers.ContextMenuProvider;
import com.novatube.common.utils.SimpleEditDialog;

public class RenameGroupMenuProvider extends ContextMenuProvider {
    private final ChannelGroupServiceWrapper mService;

    public RenameGroupMenuProvider(@NonNull Context context, int idx) {
        super(context, idx);
        mService = ChannelGroupServiceWrapper.instance(context);
    }

    @Override
    public int getTitleResId() {
        return R.string.rename_group;
    }

    @Override
    public void onClicked(Video item, VideoMenuCallback callback) {
        AppDialogPresenter.instance(getContext()).closeDialog();
        SimpleEditDialog.show(
                getContext(),
                getContext().getString(R.string.rename_group),
                item.title,
                newValue -> {
                    item.title = newValue;
                    BrowsePresenter.instance(getContext()).renameSection(item);

                    ItemGroup channelGroup = mService.findChannelGroupById(item.channelGroupId);

                    if (channelGroup != null) {
                        //channelGroup.title = newValue;
                        //mService.addChannelGroup(channelGroup);
                        mService.renameChannelGroup(channelGroup, newValue);
                    }

                    return true;
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
