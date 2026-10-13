package com.novatube.tv.ui.browse.rail;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.novatube.common.app.models.data.BrowseSection;
import com.novatube.common.app.presenters.BrowsePresenter;
import com.novatube.tv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * NovaTube round2: official-YouTube-TV-style collapsible left navigation rail.
 *
 * Collapsed: icons only. When a rail item gains focus the rail expands to reveal
 * labels; when focus moves into the content the rail collapses again.
 * D-pad: Up/Down moves between items, Right moves into content, Left from content
 * returns to the rail (plain focus search - the rail is a focusable sibling view).
 */
public class NavRailView extends LinearLayout {
    private static final long EXPAND_DURATION_MS = 220;

    private final List<NavRailItemView> mItemViews = new ArrayList<>();
    private int mActiveItemId = NavRailItem.ID_HOME;
    private boolean mExpanded;
    private ValueAnimator mWidthAnimator;
    private int mCollapsedWidth;
    private int mExpandedWidth;

    private final Runnable mCollapseCheck = () -> {
        // Runs after focus has settled: collapse only when nothing in the rail is focused.
        if (!hasFocus()) {
            setExpanded(false);
        }
    };

    /**
     * When focus enters the rail from content, land on the currently active item
     * so Up/Down starts from a predictable place.
     */
    private final ViewTreeObserver.OnGlobalFocusChangeListener mFocusListener =
            (oldFocus, newFocus) -> {
                boolean wasInside = isRailDescendant(oldFocus);
                boolean isInside = isRailDescendant(newFocus);
                if (isInside && !wasInside) {
                    NavRailItemView activeView = mItemViews.get(getActiveItemPosition());
                    if (newFocus != activeView) {
                        activeView.requestFocus();
                    }
                    // Expanding is handled by the item's own focus listener.
                }
            };

    public NavRailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        mCollapsedWidth = getResources().getDimensionPixelSize(R.dimen.nav_rail_collapsed_width);
        mExpandedWidth = getResources().getDimensionPixelSize(R.dimen.nav_rail_expanded_width);

        int itemHeight = getResources().getDimensionPixelSize(R.dimen.nav_rail_item_height);
        int itemMargin = getResources().getDimensionPixelSize(R.dimen.nav_rail_item_margin);

        for (NavRailItem item : NavRailItem.mainItems()) {
            addItemView(context, item, itemHeight, itemMargin);
        }

        // Spacer pushes Settings to the very bottom.
        View spacer = new View(context);
        addView(spacer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        addItemView(context, NavRailItem.settingsItem(), itemHeight, itemMargin);

        // Start collapsed.
        applyExpandedState(false);
        refreshChecked();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // LayoutParams only exist once inflation finished.
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp != null) {
            lp.width = mCollapsedWidth;
            setLayoutParams(lp);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(mFocusListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalFocusChangeListener(mFocusListener);
        removeCallbacks(mCollapseCheck);
        if (mWidthAnimator != null) {
            mWidthAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    private boolean isRailDescendant(View view) {
        View v = view;
        while (v != null) {
            if (v == this) {
                return true;
            }
            Object parent = v.getParent();
            v = parent instanceof View ? (View) parent : null;
        }
        return false;
    }

    private void addItemView(Context context, NavRailItem item, int itemHeight, int itemMargin) {
        NavRailItemView itemView = new NavRailItemView(context, item);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
        lp.setMargins(itemMargin, 0, itemMargin, 0);
        addView(itemView, lp);
        mItemViews.add(itemView);

        itemView.setOnClickListener(v -> onItemClicked(item));
        itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                removeCallbacks(mCollapseCheck);
                setExpanded(true);
            } else {
                // Defer: focus may be moving to another rail item.
                post(mCollapseCheck);
            }
        });
    }

    private void onItemClicked(NavRailItem item) {
        NavRailActions.perform(getContext(), item.getId());
        if (item.isSection()) {
            // Section items keep the "checked" highlight; Search/More open other screens.
            mActiveItemId = item.getId();
            refreshChecked();
        }
    }

    /**
     * Syncs the checked highlight with the section currently shown by BrowsePresenter.
     * Called from BrowseFragment whenever the selected section changes.
     */
    public void syncWithPresenter() {
        if (getContext() == null) {
            return;
        }
        BrowseSection section = BrowsePresenter.instance(getContext()).getCurrentSection();
        if (section == null) {
            return;
        }
        int railId = NavRailItem.railIdForSection(section.getId());
        if (railId != NavRailItem.ID_NONE && railId != mActiveItemId) {
            mActiveItemId = railId;
            refreshChecked();
        }
    }

    /**
     * Requests focus on the currently active rail item. Used as a D-pad
     * dead-end guard so the screen always has a focused view.
     */
    public void focusActiveItem() {
        if (!mItemViews.isEmpty()) {
            mItemViews.get(getActiveItemPosition()).requestFocus();
        }
    }

    private int getActiveItemPosition() {
        List<NavRailItem> all = allItems();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == mActiveItemId) {
                return i;
            }
        }
        return 1; // Home
    }

    private List<NavRailItem> allItems() {
        List<NavRailItem> all = new ArrayList<>(NavRailItem.mainItems());
        all.add(NavRailItem.settingsItem());
        return all;
    }

    private void refreshChecked() {
        List<NavRailItem> all = allItems();
        for (int i = 0; i < mItemViews.size() && i < all.size(); i++) {
            mItemViews.get(i).setChecked(all.get(i).getId() == mActiveItemId);
        }
    }

    private void setExpanded(boolean expanded) {
        if (mExpanded != expanded) {
            mExpanded = expanded;
            applyExpandedState(expanded);
            animateWidth(expanded ? mExpandedWidth : mCollapsedWidth);
        } else {
            // Re-apply the visual state in case it got out of sync.
            applyExpandedState(expanded);
        }
    }

    private void applyExpandedState(boolean expanded) {
        for (NavRailItemView itemView : mItemViews) {
            itemView.setExpanded(expanded);
            if (!expanded) {
                itemView.setLabelAlpha(1f);
            }
        }
    }

    private void animateWidth(int targetWidth) {
        if (mWidthAnimator != null) {
            mWidthAnimator.cancel();
        }
        int startWidth = getWidth() > 0 ? getWidth() : (mExpanded ? mExpandedWidth : mCollapsedWidth);
        if (startWidth == targetWidth) {
            return;
        }
        mWidthAnimator = ValueAnimator.ofInt(startWidth, targetWidth);
        mWidthAnimator.setDuration(EXPAND_DURATION_MS);
        mWidthAnimator.setInterpolator(new DecelerateInterpolator());
        mWidthAnimator.addUpdateListener(animation -> {
            int width = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp != null) {
                lp.width = width;
                setLayoutParams(lp);
            }
            float fraction = (float) (width - mCollapsedWidth) / (mExpandedWidth - mCollapsedWidth);
            if (targetWidth == mCollapsedWidth) {
                fraction = 1f - fraction;
            }
            fraction = Math.max(0f, Math.min(1f, fraction));
            for (NavRailItemView itemView : mItemViews) {
                itemView.setLabelAlpha(fraction);
            }
        });
        mWidthAnimator.start();
    }
}
