package com.novatube.tv.ui.playback.actions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Action;
import com.novatube.tv.R;

/**
 * An action for displaying a channel icon.
 * NovaTube round2: also carries the channel name, shown next to the icon
 * in the left player cluster (official YT TV style).
 */
public class ChannelAction extends PaddingAction {
    public ChannelAction(Context context) {
        super(R.id.action_channel);
        Drawable uncoloredDrawable = ContextCompat.getDrawable(context, R.drawable.action_channel);

        setIcon(uncoloredDrawable);
        setLabel1(context.getString(
                R.string.action_channel));
    }

    /**
     * Sets the channel name shown next to the channel icon in the left cluster.
     */
    public void setChannelName(String channelName) {
        if (channelName != null && !channelName.isEmpty()) {
            setLabel1(channelName);
        }
    }
}
