package com.novatube.tv.ui.playback.actions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Action;
import com.novatube.tv.R;

/**
 * NovaTube: settings gear button for the player controls (official YT TV style).
 * Opens the player settings dialog.
 */
public class SettingsAction extends Action {
    public SettingsAction(Context context) {
        super(R.id.action_settings);
        Drawable uncoloredDrawable = ContextCompat.getDrawable(context, R.drawable.icon_settings);

        setIcon(uncoloredDrawable);
        setLabel1(context.getString(
                R.string.action_settings));
    }
}
