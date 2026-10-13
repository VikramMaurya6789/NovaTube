package com.novatube.tv.ui.browse.rail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.novatube.tv.R;

/**
 * NovaTube round2: one focusable entry of the left navigation rail.
 * Collapsed state: icon only, centered. Expanded state: icon + label.
 * Focused/checked state: floating pill background, white icon, slight elevation.
 */
public class NavRailItemView extends LinearLayout {
    private final ImageView mIcon;
    private final TextView mLabel;
    private final int mPaddingStartExpanded;
    private final float mElevationFocused;
    private boolean mChecked;
    // Stored instead of passed to super: the view also needs its own focus handling.
    private OnFocusChangeListener mExternalFocusListener;

    public NavRailItemView(Context context, NavRailItem item) {
        super(context);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        setFocusable(true);
        setClickable(true);
        setBackgroundResource(R.drawable.nav_rail_item_bg);

        LayoutInflater.from(context).inflate(R.layout.nav_rail_item, this, true);
        mIcon = findViewById(R.id.rail_item_icon);
        mLabel = findViewById(R.id.rail_item_label);

        mIcon.setImageResource(item.getIconRes());
        mLabel.setText(item.getTitleRes());
        setContentDescription(context.getText(item.getTitleRes()));

        mPaddingStartExpanded = getResources().getDimensionPixelSize(R.dimen.nav_rail_item_padding_start);
        mElevationFocused = getResources().getDimension(R.dimen.nav_rail_item_elevation);

        // Start collapsed: icon only.
        setExpanded(false);

        refreshState();
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        // Kept locally so the view's own focus visuals (tint/elevation) keep working.
        mExternalFocusListener = listener;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        refreshState();
        if (mExternalFocusListener != null) {
            mExternalFocusListener.onFocusChange(this, gainFocus);
        }
    }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshState();
        }
    }

    public void setExpanded(boolean expanded) {
        mLabel.setVisibility(expanded ? VISIBLE : GONE);
        if (expanded) {
            setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            setPaddingRelative(mPaddingStartExpanded, getPaddingTop(), getPaddingEnd(), getPaddingBottom());
        } else {
            setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            setPaddingRelative(0, getPaddingTop(), getPaddingEnd(), getPaddingBottom());
        }
    }

    public void setLabelAlpha(float alpha) {
        mLabel.setAlpha(alpha);
    }

    private void refreshState() {
        boolean highlighted = mChecked || isFocused();
        int color = highlighted ? Color.WHITE : ContextCompat.getColor(getContext(), R.color.nav_rail_icon_idle);

        // setColorFilter works on all API levels (minSdk 17); setImageTintList needs API 21+.
        mIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mLabel.setTextColor(color);

        // Slight elevation on the focused item ("floating" pill). API 21+ only.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(isFocused() ? mElevationFocused : 0f);
        }
    }
}
