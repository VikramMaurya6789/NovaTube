package com.novatube.common.prefs;

import android.content.Context;

import com.novatube.common.prefs.common.DataSaverBase;

public class NetworkData extends DataSaverBase {
    private static NetworkData sInstance;

    private NetworkData(Context context) {
        super(context, true);
    }

    public static NetworkData instance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkData(context);
        }

        return sInstance;
    }

    public boolean isConscryptEnabled() {
        return getBoolean(0, false);
    }

    public void setConscryptEnabled(boolean enable) {
        setBoolean(0, enable);
    }
}
