package com.novatube.tv.presenter;

import android.content.Context;
import android.util.Pair;
import com.novatube.common.prefs.MainUIData;
import com.novatube.tv.R;
import com.novatube.tv.ui.browse.video.GridFragmentHelper;

public class ShortsCardPresenter extends VideoCardPresenter {
    @Override
    protected Pair<Integer, Integer> getCardDimensPx(Context context) {
        return GridFragmentHelper.getCardDimensPx(context, R.dimen.shorts_card_width, R.dimen.shorts_card_height, MainUIData.instance(context).getVideoGridScale());
    }
}
