/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.novatube.tv.ui.mod.leanback.playerglue.tooltips;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.leanback.R;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.PlaybackControlsRow;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.core.content.ContextCompat;
import com.novatube.tv.ui.playback.actions.PaddingAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.novatube.tv.ui.playback.actions.TwoStateAction;

/**
 * Displays primary and secondary controls for a {@link PlaybackControlsRow}.
 *
 * Binds to items of type {@link Action}.
 */
public class ControlButtonPresenterSelector extends PresenterSelector {
    /** Default icon-only button. */
    public static final int MODE_DEFAULT = 0;
    /** NovaTube round2: right-cluster like/dislike/comment/save rendered as one pill. */
    public static final int MODE_PILL = 1;
    /** NovaTube round2: left-cluster icon + text label button. */
    public static final int MODE_LABELED = 2;

    /**
     * NovaTube round2: ids of the like/dislike/comment/save buttons actually present
     * in the right cluster, in display order. Segment backgrounds (left/mid/right)
     * follow this order so the pill stays seamless when a button is toggled off.
     */
    private static final List<Integer> DEFAULT_PILL_ORDER = Arrays.asList(
            com.novatube.tv.R.id.action_thumbs_up,
            com.novatube.tv.R.id.action_thumbs_down,
            com.novatube.tv.R.id.action_comment,
            com.novatube.tv.R.id.action_playlist_add);

    private List<Integer> mPillOrder = new ArrayList<>(DEFAULT_PILL_ORDER);

    public void setPillOrder(List<Integer> idsInOrder) {
        mPillOrder = idsInOrder != null ? idsInOrder : new ArrayList<>();
        // NovaTube round2: the pill order is consumed by the (static) button presenters,
        // so propagate it to them.
        mPrimaryPresenter.setPillOrder(mPillOrder);
        mSecondaryPresenter.setPillOrder(mPillOrder);
    }

    private final ControlButtonPresenter mPrimaryPresenter =
            new ControlButtonPresenter(R.layout.lb_control_button_primary);
    private final ControlButtonPresenter mSecondaryPresenter =
            new ControlButtonPresenter(R.layout.lb_control_button_secondary);
    private final Presenter[] mPresenters = new Presenter[]{mPrimaryPresenter};

    public ControlButtonPresenterSelector(boolean tooltipsEnabled) {
        mPrimaryPresenter.tooltipsEnabled = mSecondaryPresenter.tooltipsEnabled = tooltipsEnabled;
        // NovaTube round2: like/dislike/comment/save form a single pill (official YT TV style)
        mSecondaryPresenter.setMode(MODE_PILL);
    }

    /**
     * NovaTube round2: creates a button presenter with a custom layout and presentation mode.
     */
    public static ControlButtonPresenter createButtonPresenter(int layoutResourceId, int mode,
                                                              boolean tooltipsEnabled) {
        ControlButtonPresenter presenter = new ControlButtonPresenter(layoutResourceId, mode);
        presenter.tooltipsEnabled = tooltipsEnabled;
        return presenter;
    }

    /**
     * Returns the presenter for primary controls.
     */
    public Presenter getPrimaryPresenter() {
        return mPrimaryPresenter;
    }

    /**
     * Returns the presenter for secondary controls.
     */
    public Presenter getSecondaryPresenter() {
        return mSecondaryPresenter;
    }

    /**
     * Always returns the presenter for primary controls.
     */
    @Override
    public Presenter getPresenter(Object item) {
        return mPrimaryPresenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return mPresenters;
    }

    static class ActionViewHolder extends Presenter.ViewHolder {
        ImageView mIcon;
        TextView mLabel;
        View mFocusableView;

        public ActionViewHolder(View view) {
            super(view);
            mIcon = (ImageView) view.findViewById(R.id.icon);
            mLabel = (TextView) view.findViewById(R.id.label);
            mFocusableView = view.findViewById(R.id.button);
        }
    }

    public static class ControlButtonPresenter extends Presenter {
        private int mLayoutResourceId;
        private boolean tooltipsEnabled;
        private int mMode = MODE_DEFAULT;
        /** NovaTube round2: pill segment order, propagated from the selector. */
        private List<Integer> mPillOrder = new ArrayList<>(DEFAULT_PILL_ORDER);

        ControlButtonPresenter(int layoutResourceId) {
            mLayoutResourceId = layoutResourceId;
        }

        /** NovaTube round2: presentation mode (default / pill / labeled). */
        public ControlButtonPresenter(int layoutResourceId, int mode) {
            mLayoutResourceId = layoutResourceId;
            mMode = mode;
        }

        public void setMode(int mode) {
            mMode = mode;
        }

        /** NovaTube round2: ids of the pill buttons in display order. */
        public void setPillOrder(List<Integer> idsInOrder) {
            mPillOrder = idsInOrder != null ? idsInOrder : new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(mLayoutResourceId, parent, false);
            return new ActionViewHolder(v);
        }

        // Used inside: com.novatube.tv.ui.mod.leanback.playerglue.tweaks.ControlBarPresenter.ViewHolder.bindControlToAction()
        // Restore focus: com.novatube.tv.ui.mod.leanback.playerglue.tweaks.ControlBar.onRequestFocusInDescendants()
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            Action action = (Action) item;
            ActionViewHolder vh = (ActionViewHolder) viewHolder;

            vh.mIcon.setImageDrawable(action.getIcon());
            if (action instanceof PaddingAction) {
                int padding = ((PaddingAction) action).getPadding();
                if (padding > 0) {
                    vh.mIcon.setPadding(padding, padding, padding, padding);
                }
            }
            if (vh.mLabel != null) {
                if (action.getIcon() == null) {
                    vh.mLabel.setText(action.getLabel1());
                } else {
                    vh.mLabel.setText(null);
                }
            }
            // NovaTube round2: official YT TV cluster styling (pill / labeled buttons)
            applyClusterStyle(vh, action);
            CharSequence contentDescription = TextUtils.isEmpty(action.getLabel2())
                    ? action.getLabel1() : action.getLabel2();
            if (!TextUtils.equals(vh.mFocusableView.getContentDescription(), contentDescription)) {
                vh.mFocusableView.setContentDescription(contentDescription);
                vh.mFocusableView.sendAccessibilityEvent(
                        AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);

                // MOD: enable control tooltips
                if (tooltipsEnabled) {
                    TooltipCompatHandler.setTooltipText(vh.mFocusableView, action.getLabel1());
                }
            }
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            ActionViewHolder vh = (ActionViewHolder) viewHolder;
            vh.mIcon.setImageDrawable(null);
            if (vh.mLabel != null) {
                vh.mLabel.setText(null);
            }
            vh.mFocusableView.setContentDescription(null);
        }

        /**
         * NovaTube round2: official YT TV cluster styling.
         * PILL mode groups like/dislike/comment/save into one pill (right cluster).
         * LABELED mode renders icon + text label (left cluster: channel / Description / Subscribe).
         */
        private void applyClusterStyle(ActionViewHolder vh, Action action) {
            int actionId = (int) action.getId();
            Context context = vh.view.getContext();

            if (mMode == MODE_PILL) {
                int bgRes = 0;
                int pillIndex = mPillOrder.indexOf(actionId);
                if (pillIndex != -1) {
                    if (pillIndex == 0) {
                        bgRes = com.novatube.tv.R.drawable.nt_pill_left;
                    } else if (pillIndex == mPillOrder.size() - 1) {
                        bgRes = com.novatube.tv.R.drawable.nt_pill_right;
                    } else {
                        bgRes = com.novatube.tv.R.drawable.nt_pill_mid;
                    }
                }
                if (bgRes != 0) {
                    vh.mFocusableView.setBackgroundResource(bgRes);
                    vh.mFocusableView.setOnFocusChangeListener((v, hasFocus) ->
                            updatePillIconTint(vh, hasFocus));
                    // Views are recycled: apply the current focus state right away
                    updatePillIconTint(vh, vh.mFocusableView.isFocused());
                } else {
                    // Recycled view: restore the default button look
                    vh.mFocusableView.setBackgroundResource(0);
                    vh.mFocusableView.setOnFocusChangeListener(null);
                    vh.mIcon.clearColorFilter();
                }
            } else if (mMode == MODE_LABELED) {
                vh.mFocusableView.setOnFocusChangeListener(null);
                vh.mIcon.clearColorFilter();
                if (vh.mLabel != null) {
                    vh.mLabel.setVisibility(View.VISIBLE);
                    if (actionId == com.novatube.tv.R.id.action_subscribe) {
                        // High-contrast white Subscribe pill, text only
                        vh.mFocusableView.setBackgroundResource(
                                com.novatube.tv.R.drawable.nt_subscribe_pill);
                        vh.mIcon.setVisibility(View.GONE);
                        vh.mLabel.setTextColor(ContextCompat.getColor(context,
                                com.novatube.tv.R.color.nt_subscribe_text));
                        vh.mLabel.setText(isActionOn(action)
                                ? context.getString(com.novatube.tv.R.string.subscribed_to_channel)
                                : context.getString(com.novatube.tv.R.string.action_subscribe));
                    } else if (actionId == com.novatube.tv.R.id.action_info) {
                        // Grey Description pill, text only
                        vh.mFocusableView.setBackgroundResource(
                                com.novatube.tv.R.drawable.nt_pill_full);
                        vh.mIcon.setVisibility(View.GONE);
                        vh.mLabel.setTextColor(Color.WHITE);
                        vh.mLabel.setText(action.getLabel1());
                    } else {
                        // Channel: icon + name, transparent background
                        vh.mFocusableView.setBackgroundResource(
                                com.novatube.tv.R.drawable.nt_left_plain);
                        vh.mIcon.setVisibility(View.VISIBLE);
                        vh.mLabel.setTextColor(Color.WHITE);
                        vh.mLabel.setText(action.getLabel1());
                    }
                }
            }
        }

        /**
         * Focused pill segment turns white: switch the icon to dark for contrast.
         */
        private void updatePillIconTint(ActionViewHolder vh, boolean hasFocus) {
            if (hasFocus) {
                vh.mIcon.setColorFilter(ContextCompat.getColor(vh.view.getContext(),
                        com.novatube.tv.R.color.nt_pill_icon_focused));
            } else {
                vh.mIcon.clearColorFilter();
            }
        }

        private boolean isActionOn(Action action) {
            return action instanceof androidx.leanback.widget.PlaybackControlsRow.MultiAction
                    && ((androidx.leanback.widget.PlaybackControlsRow.MultiAction) action).getIndex()
                    == TwoStateAction.INDEX_ON;
        }

        @Override
        public void setOnClickListener(ViewHolder viewHolder,
                                       View.OnClickListener listener) {
            ((ActionViewHolder) viewHolder).mFocusableView.setOnClickListener(listener);
        }

        public void setOnLongClickListener(ViewHolder viewHolder,
                                       View.OnLongClickListener listener) {
            ((ActionViewHolder) viewHolder).mFocusableView.setOnLongClickListener(listener);
        }
    }
}
