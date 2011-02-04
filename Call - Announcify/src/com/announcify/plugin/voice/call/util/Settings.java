package com.announcify.plugin.voice.call.util;

import android.content.Context;

import com.announcify.api.background.util.PluginSettings;
import com.announcify.plugin.voice.call.R;


public class Settings extends PluginSettings {

    public static final String ACTION_SETTINGS = "com.announcify.plugin.voice.call.SETTINGS";

    public static final String PREFERENCES_NAME = "com.announcify.plugin.voice.call";

    public Settings(final Context context) {
        super(context, PREFERENCES_NAME);
    }

    @Override
    public String getEventType() {
        return context.getString(R.string.event_call);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public int getReadingRepeat() {
        return Integer.parseInt(preferences.getString(KEY_READING_REPEAT, "5"));
    }

    @Override
    public String getSettingsAction() {
        return ACTION_SETTINGS;
    }
}
