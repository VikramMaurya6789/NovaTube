package com.novatube.tv.ui.browse.rail;

import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.novatube.tv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * NovaTube round2: data model for one left navigation rail entry.
 * Official YouTube TV order: Search, Home, Music, Movies & TV, Podcasts,
 * Subscriptions, Library, More, with Settings pinned at the bottom.
 */
public class NavRailItem {
    public static final int ID_NONE = -1;
    public static final int ID_SEARCH = 1;
    public static final int ID_HOME = 2;
    public static final int ID_MUSIC = 3;
    public static final int ID_MOVIES_TV = 4;
    public static final int ID_PODCASTS = 5;
    public static final int ID_SUBSCRIPTIONS = 6;
    public static final int ID_LIBRARY = 7;
    public static final int ID_MORE = 8;
    public static final int ID_SETTINGS = 9;

    private final int mId;
    private final int mTitleRes;
    private final int mIconRes;
    private final boolean mIsSection; // true when the item maps to a browse section (can stay "checked")

    private NavRailItem(int id, int titleRes, int iconRes, boolean isSection) {
        mId = id;
        mTitleRes = titleRes;
        mIconRes = iconRes;
        mIsSection = isSection;
    }

    public int getId() {
        return mId;
    }

    public int getTitleRes() {
        return mTitleRes;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public boolean isSection() {
        return mIsSection;
    }

    /**
     * Top part of the rail (Settings is added separately, pinned at the bottom).
     */
    public static List<NavRailItem> mainItems() {
        List<NavRailItem> items = new ArrayList<>();
        items.add(new NavRailItem(ID_SEARCH, R.string.nav_rail_search, R.drawable.ic_rail_search, false));
        items.add(new NavRailItem(ID_HOME, R.string.nav_rail_home, R.drawable.ic_rail_home, true));
        items.add(new NavRailItem(ID_MUSIC, R.string.nav_rail_music, R.drawable.ic_rail_music, true));
        items.add(new NavRailItem(ID_MOVIES_TV, R.string.nav_rail_movies_tv, R.drawable.ic_rail_movies, false));
        items.add(new NavRailItem(ID_PODCASTS, R.string.nav_rail_podcasts, R.drawable.ic_rail_podcasts, false));
        items.add(new NavRailItem(ID_SUBSCRIPTIONS, R.string.nav_rail_subscriptions, R.drawable.ic_rail_subscriptions, true));
        items.add(new NavRailItem(ID_LIBRARY, R.string.nav_rail_library, R.drawable.ic_rail_library, true));
        items.add(new NavRailItem(ID_MORE, R.string.nav_rail_more, R.drawable.ic_rail_more, false));
        return items;
    }

    public static NavRailItem settingsItem() {
        return new NavRailItem(ID_SETTINGS, R.string.nav_rail_settings, R.drawable.ic_rail_settings, true);
    }

    /**
     * Maps a browse section id (MediaGroup type) to the rail item that represents it.
     * Returns {@link #ID_NONE} when no rail item represents the section
     * (e.g. History, Trending - those live under "More").
     */
    public static int railIdForSection(int sectionId) {
        switch (sectionId) {
            case MediaGroup.TYPE_HOME:
                return ID_HOME;
            case MediaGroup.TYPE_MUSIC:
                return ID_MUSIC;
            case MediaGroup.TYPE_SUBSCRIPTIONS:
                return ID_SUBSCRIPTIONS;
            case MediaGroup.TYPE_USER_PLAYLISTS:
                return ID_LIBRARY;
            case MediaGroup.TYPE_SETTINGS:
                return ID_SETTINGS;
            default:
                return ID_NONE;
        }
    }
}
