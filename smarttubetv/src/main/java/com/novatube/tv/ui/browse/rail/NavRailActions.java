package com.novatube.tv.ui.browse.rail;

import android.app.AlertDialog;
import android.content.Context;

import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.novatube.common.app.presenters.BrowsePresenter;
import com.novatube.common.app.presenters.SearchPresenter;
import com.novatube.tv.R;

/**
 * NovaTube round2: maps rail items to existing destinations.
 * No new content pipelines are built here - every item reuses an existing
 * screen/section. Mapping:
 *
 * Search       -> SearchTagsActivity (via SearchPresenter.startSearch(null), same as the old search orb)
 * Home         -> Home browse section (TYPE_HOME)
 * Music        -> Music browse section (TYPE_MUSIC)
 * Movies & TV  -> no dedicated section exists -> prefilled search "movies" (closest existing screen)
 * Podcasts     -> no dedicated section exists -> prefilled search "podcasts" (closest existing screen)
 * Subscriptions-> Subscriptions grid section (TYPE_SUBSCRIPTIONS)
 * Library      -> no Library section exists -> user playlists section (TYPE_USER_PLAYLISTS, closest match)
 * More         -> overflow dialog with the remaining browse sections
 * Settings     -> Settings grid section (TYPE_SETTINGS)
 */
public final class NavRailActions {
    private NavRailActions() {
    }

    public static void perform(Context context, int railId) {
        if (context == null) {
            return;
        }

        switch (railId) {
            case NavRailItem.ID_SEARCH:
                SearchPresenter.instance(context).startSearch(null);
                break;
            case NavRailItem.ID_HOME:
                selectSection(context, MediaGroup.TYPE_HOME);
                break;
            case NavRailItem.ID_MUSIC:
                selectSection(context, MediaGroup.TYPE_MUSIC);
                break;
            case NavRailItem.ID_MOVIES_TV:
                // No Movies section is registered in the app (MediaGroup.TYPE_MOVIES has no section mapping).
                SearchPresenter.instance(context).startSearch("movies");
                break;
            case NavRailItem.ID_PODCASTS:
                // No Podcasts section exists in the app.
                SearchPresenter.instance(context).startSearch("podcasts");
                break;
            case NavRailItem.ID_SUBSCRIPTIONS:
                selectSection(context, MediaGroup.TYPE_SUBSCRIPTIONS);
                break;
            case NavRailItem.ID_LIBRARY:
                // No Library section exists; the user's playlists is the closest existing screen.
                selectSection(context, MediaGroup.TYPE_USER_PLAYLISTS);
                break;
            case NavRailItem.ID_MORE:
                showMoreDialog(context);
                break;
            case NavRailItem.ID_SETTINGS:
                selectSection(context, MediaGroup.TYPE_SETTINGS);
                break;
            default:
                break;
        }
    }

    private static void selectSection(Context context, int sectionId) {
        // Same call the launcher activities use; enables the section if needed and switches to it.
        BrowsePresenter.instance(context).selectSection(sectionId);
    }

    /**
     * Overflow dialog listing the browse sections that don't have a rail item.
     * D-pad navigable list; selecting an item switches to that section.
     */
    private static void showMoreDialog(Context context) {
        final int[] sectionIds = {
                MediaGroup.TYPE_HISTORY,
                MediaGroup.TYPE_TRENDING,
                MediaGroup.TYPE_LIVE,
                MediaGroup.TYPE_SPORTS,
                MediaGroup.TYPE_GAMING,
                MediaGroup.TYPE_NEWS,
                MediaGroup.TYPE_SHORTS,
                MediaGroup.TYPE_CHANNEL_UPLOADS,
                MediaGroup.TYPE_MY_VIDEOS,
                MediaGroup.TYPE_NOTIFICATIONS,
                MediaGroup.TYPE_PLAYBACK_QUEUE,
                MediaGroup.TYPE_BLOCKED_CHANNELS,
                MediaGroup.TYPE_KIDS_HOME
        };

        CharSequence[] labels = new CharSequence[sectionIds.length];
        for (int i = 0; i < sectionIds.length; i++) {
            labels[i] = labelForSection(context, sectionIds[i]);
        }

        new AlertDialog.Builder(context)
                .setTitle(R.string.nav_rail_more_title)
                .setItems(labels, (dialog, which) -> selectSection(context, sectionIds[which]))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private static CharSequence labelForSection(Context context, int sectionId) {
        switch (sectionId) {
            case MediaGroup.TYPE_HISTORY:
                return context.getString(R.string.header_history);
            case MediaGroup.TYPE_TRENDING:
                return context.getString(R.string.header_trending);
            case MediaGroup.TYPE_LIVE:
                return context.getString(R.string.badge_live);
            case MediaGroup.TYPE_SPORTS:
                return context.getString(R.string.header_sports);
            case MediaGroup.TYPE_GAMING:
                return context.getString(R.string.header_gaming);
            case MediaGroup.TYPE_NEWS:
                return context.getString(R.string.header_news);
            case MediaGroup.TYPE_SHORTS:
                return context.getString(R.string.header_shorts);
            case MediaGroup.TYPE_CHANNEL_UPLOADS:
                return context.getString(R.string.header_channels);
            case MediaGroup.TYPE_MY_VIDEOS:
                return context.getString(R.string.my_videos);
            case MediaGroup.TYPE_NOTIFICATIONS:
                return context.getString(R.string.header_notifications);
            case MediaGroup.TYPE_PLAYBACK_QUEUE:
                return context.getString(R.string.playback_queue_category_title);
            case MediaGroup.TYPE_BLOCKED_CHANNELS:
                return context.getString(R.string.header_blocked_channels);
            case MediaGroup.TYPE_KIDS_HOME:
                return context.getString(R.string.header_kids_home);
            default:
                return String.valueOf(sectionId);
        }
    }
}
