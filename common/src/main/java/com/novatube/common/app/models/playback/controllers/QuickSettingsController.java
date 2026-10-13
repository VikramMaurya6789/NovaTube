package com.novatube.common.app.models.playback.controllers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.novatube.common.R;
import com.novatube.common.app.models.playback.BasePlayerController;
import com.novatube.common.app.models.playback.manager.PlayerConstants;
import com.novatube.common.app.models.playback.ui.OptionCategory;
import com.novatube.common.app.models.playback.ui.OptionItem;
import com.novatube.common.app.models.playback.ui.UiOptionItem;
import com.novatube.common.app.presenters.AppDialogPresenter;
import com.novatube.common.exoplayer.other.SubtitleManager.SubtitleStyle;
import com.novatube.common.exoplayer.selector.FormatItem;
import com.novatube.common.misc.AppDataSourceManager;
import com.novatube.common.utils.AppDialogUtil;

import java.util.List;
import java.util.Locale;

/**
 * NovaTube round2: compact YouTube-style quick settings popup shown when the
 * player settings gear is pressed. Each row reuses the existing underlying
 * setting dialogs (quality presets/formats, speed list, subtitle styles,
 * audio language, playback mode).
 */
public class QuickSettingsController extends BasePlayerController {
    private static final String TAG = QuickSettingsController.class.getSimpleName();
    private static final int VIDEO_FORMATS_ID = 132;
    private static final int POPUP_WIDTH_DP = 560;
    private static final int POPUP_BOTTOM_MARGIN_DP = 120;

    @Override
    public void onButtonClicked(int buttonId, int buttonState) {
        if (buttonId == R.id.action_settings) {
            showQuickSettings();
        }
    }

    private void showQuickSettings() {
        Context context = getContext();
        if (!(context instanceof Activity)) {
            return;
        }
        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        View anchor = activity.getWindow().getDecorView();
        if (anchor == null) {
            return;
        }

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundResource(R.drawable.nt_quick_settings_bg);
        int outerPad = dp(8);
        content.setPadding(outerPad, outerPad, outerPad, outerPad);

        final PopupWindow[] popupRef = new PopupWindow[1];

        content.addView(createRow(context,
                context.getString(R.string.quick_settings_quality), getQualityLabel(),
                () -> { dismissPopup(popupRef); openQualityDialog(); }));
        content.addView(createRow(context,
                context.getString(R.string.quick_settings_speed), getSpeedLabel(),
                () -> { dismissPopup(popupRef); openSpeedDialog(); }));
        content.addView(createRow(context,
                context.getString(R.string.subtitle_style), getCaptionsLabel(),
                () -> { dismissPopup(popupRef); openCaptionsDialog(); }));
        content.addView(createRow(context,
                context.getString(R.string.audio_language), getAudioLabel(),
                () -> { dismissPopup(popupRef); openAudioDialog(); }));
        content.addView(createRow(context,
                context.getString(R.string.quick_settings_loop), getLoopLabel(),
                () -> { dismissPopup(popupRef); openLoopDialog(); }));

        PopupWindow popup = new PopupWindow(content, dp(POPUP_WIDTH_DP),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popupRef[0] = popup;

        content.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                popup.dismiss();
                return true;
            }
            return false;
        });

        popup.showAtLocation(anchor, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, dp(POPUP_BOTTOM_MARGIN_DP));

        if (content.getChildCount() > 0) {
            content.getChildAt(0).requestFocus();
        }
    }

    private void dismissPopup(PopupWindow[] popupRef) {
        if (popupRef[0] != null) {
            popupRef[0].dismiss();
            popupRef[0] = null;
        }
    }

    private View createRow(Context context, String title, String value, Runnable onClick) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setBackgroundResource(R.drawable.nt_quick_settings_row);
        int hPad = dp(16);
        int vPad = dp(14);
        row.setPadding(hPad, vPad, hPad, vPad);

        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        row.addView(titleView, titleLp);

        TextView valueView = new TextView(context);
        valueView.setText(value);
        valueView.setTextColor(0xFFB3B3B3);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        row.addView(valueView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        row.setOnClickListener(v -> onClick.run());

        return row;
    }

    private String getQualityLabel() {
        FormatItem format = getPlayerData().getFormat(FormatItem.TYPE_VIDEO);
        if (format == null) {
            return getContext().getString(R.string.quick_settings_auto);
        }
        String presetName = findPresetName(format);
        if (presetName != null) {
            return presetName;
        }
        CharSequence title = format.getTitle();
        return title != null ? title.toString() : getContext().getString(R.string.quick_settings_auto);
    }

    private String findPresetName(FormatItem format) {
        if (format == null) {
            return null;
        }
        try {
            for (FormatItem.VideoPreset preset : AppDataSourceManager.instance().getVideoPresets()) {
                if (preset != null && preset.format != null && preset.format.equals(format)) {
                    return preset.name;
                }
            }
        } catch (Exception e) {
            // NOP
        }
        return null;
    }

    private String getSpeedLabel() {
        float speed = getPlayer() != null ? getPlayer().getSpeed() : getPlayerData().getSpeed();
        if (Math.abs(speed - 1f) < 0.001f) {
            return getContext().getString(R.string.quick_settings_normal);
        }
        String text = String.valueOf(speed);
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        return text + "x";
    }

    private String getCaptionsLabel() {
        SubtitleStyle style = getPlayerData().getSubtitleStyle();
        if (style == null) {
            return "";
        }
        return getContext().getString(style.nameResId);
    }

    private String getAudioLabel() {
        String lang = getPlayerData().getAudioLanguage();
        if (TextUtils.isEmpty(lang)) {
            return getContext().getString(R.string.original_lang);
        }
        return new Locale(lang).getDisplayLanguage();
    }

    private String getLoopLabel() {
        switch (getPlayerData().getPlaybackMode()) {
            case PlayerConstants.PLAYBACK_MODE_ALL:
                return getContext().getString(R.string.repeat_mode_all);
            case PlayerConstants.PLAYBACK_MODE_ONE:
                return getContext().getString(R.string.repeat_mode_one);
            case PlayerConstants.PLAYBACK_MODE_SHUFFLE:
                return getContext().getString(R.string.repeat_mode_shuffle);
            case PlayerConstants.PLAYBACK_MODE_LIST:
                return getContext().getString(R.string.repeat_mode_pause_alt);
            case PlayerConstants.PLAYBACK_MODE_REVERSE_LIST:
                return getContext().getString(R.string.repeat_mode_reverse_list);
            case PlayerConstants.PLAYBACK_MODE_PAUSE:
                return getContext().getString(R.string.repeat_mode_pause);
            case PlayerConstants.PLAYBACK_MODE_CLOSE:
            default:
                return getContext().getString(R.string.repeat_mode_none);
        }
    }

    private void openQualityDialog() {
        if (getPlayer() == null) {
            return;
        }
        AppDialogPresenter settingsPresenter = AppDialogPresenter.instance(getContext());

        settingsPresenter.appendCategory(AppDialogUtil.createVideoPresetsCategory(
                getContext(), this::onPresetSelected));

        List<FormatItem> videoFormats = getPlayer().getVideoFormats();
        settingsPresenter.appendCategory(OptionCategory.from(
                VIDEO_FORMATS_ID,
                OptionCategory.TYPE_RADIO_LIST,
                getContext().getString(R.string.title_video_formats),
                UiOptionItem.from(videoFormats, this::selectFormatOption,
                        getContext().getString(R.string.option_disabled))));

        settingsPresenter.showDialog(getContext().getString(R.string.quick_settings_quality));
    }

    private void onPresetSelected() {
        if (getPlayer() == null) {
            return;
        }
        FormatItem format = getPlayerData().getFormat(FormatItem.TYPE_VIDEO);
        if (format == null) {
            return;
        }
        getPlayer().setFormat(format);
        if (!getPlayer().containsMedia()) {
            getPlayer().reloadPlayback();
        }
        getPlayer().showOverlay(false);
    }

    private void selectFormatOption(OptionItem option) {
        if (getPlayer() == null) {
            return;
        }
        FormatItem formatItem = UiOptionItem.toFormat(option);
        if (formatItem == null) {
            return;
        }
        getPlayer().setFormat(formatItem);
        persistFormat(formatItem);
        if (!getPlayer().containsMedia()) {
            getPlayer().reloadPlayback();
        }
        getPlayer().showOverlay(false);
    }

    private void persistFormat(FormatItem formatItem) {
        FormatItem stored = getPlayerData().getFormat(formatItem.getType());
        if (formatItem.getType() == FormatItem.TYPE_VIDEO) {
            if (stored == null || !stored.isPreset()) {
                getPlayerData().setFormat(formatItem);
            } else {
                getPlayerData().setTempVideoFormat(formatItem);
            }
        } else {
            getPlayerData().setFormat(formatItem);
        }
    }

    private void openSpeedDialog() {
        AppDialogPresenter settingsPresenter = AppDialogPresenter.instance(getContext());
        settingsPresenter.appendCategory(AppDialogUtil.createSpeedListCategory(getContext(), getPlayer()));
        settingsPresenter.showDialog(getContext().getString(R.string.video_speed));
    }

    private void openCaptionsDialog() {
        AppDialogPresenter settingsPresenter = AppDialogPresenter.instance(getContext());
        settingsPresenter.appendCategory(AppDialogUtil.createSubtitleStylesCategory(getContext()));
        settingsPresenter.showDialog(getContext().getString(R.string.subtitle_style));
    }

    private void openAudioDialog() {
        AppDialogPresenter settingsPresenter = AppDialogPresenter.instance(getContext());
        settingsPresenter.appendCategory(AppDialogUtil.createAudioLanguageCategory(
                getContext(), () -> {
                    if (getPlayer() != null) {
                        getPlayer().restartEngine();
                    }
                }));
        settingsPresenter.showDialog(getContext().getString(R.string.audio_language));
    }

    private void openLoopDialog() {
        AppDialogPresenter settingsPresenter = AppDialogPresenter.instance(getContext());
        settingsPresenter.appendCategory(AppDialogUtil.createPlaybackModeCategory(getContext()));
        settingsPresenter.showDialog(getContext().getString(R.string.action_repeat_mode));
    }

    private int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
