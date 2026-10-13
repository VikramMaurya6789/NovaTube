package com.novatube.tv.presenter;

import android.content.Context;
import android.util.Pair;
import com.novatube.common.prefs.MainUIData;
import com.novatube.tv.R;
import com.novatube.tv.ui.browse.video.GridFragmentHelper;

public class TinyCardPresenter extends VideoCardPresenter {
    @Override
    protected Pair<Integer, Integer> getCardDimensPx(Context context) {
        return GridFragmentHelper.getCardDimensPx(context, R.dimen.tiny_card_width, R.dimen.tiny_card_height, MainUIData.instance(context).getVideoGridScale());
    }

    @Override
    protected boolean isCardMultilineTitleEnabled(Context context) {
        return false;
    }

    @Override
    protected boolean isContentEnabled() {
        return false;
    }
}
