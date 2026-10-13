package com.novatube.tv.ui.playback.actions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Action;
import com.novatube.tv.R;

/**
 * NovaTube round2: Comments button for the right-side pill cluster (official YT TV style).
 * Reuses the existing action_chat behavior (comments dialog / live chat toggle).
 */
public class CommentAction extends Action {
    public CommentAction(Context context) {
        super(R.id.action_comment);
        Drawable uncoloredDrawable = ContextCompat.getDrawable(context, R.drawable.action_chat);

        setIcon(uncoloredDrawable);
        setLabel1(context.getString(R.string.action_comment));
    }
}
