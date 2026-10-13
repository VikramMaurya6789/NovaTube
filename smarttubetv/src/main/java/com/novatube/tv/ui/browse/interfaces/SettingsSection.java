package com.novatube.tv.ui.browse.interfaces;

import com.novatube.common.app.models.data.SettingsGroup;

public interface SettingsSection extends Section {
    void update(SettingsGroup items);
}
