package com.announcify.plugin.call.util;

import com.announcify.util.PluginSettings;

import android.content.Context;
import android.content.SharedPreferences;

public class CallnouncifySettings implements PluginSettings{
	public static final String PREFERENCES_NAME = "com.announcify.plugin.call.SETTINGS";

	private final SharedPreferences preferences;

	private static final String KEY_READING_BREAK = "preference_reading_break";
	private static final String KEY_READING_REPEAT = "preference_reading_repeat";
	private static final String KEY_READING_WAIT = "preference_reading_wait";

	private static final String KEY_READING_UNKNOWN = "preference_reading_unknown";
	private static final String KEY_READING_DISCREET = "preference_reading_discreet";
	private static final String KEY_READING_ANNOUNCEMENT = "preference_reading_announcement";

	public CallnouncifySettings(final Context context) {
		preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
	}

	public int getReadingBreak() {
		return Integer.parseInt(preferences.getString(KEY_READING_BREAK, "1"));
	}

	public int getReadingRepeat() {
		return Integer.parseInt(preferences.getString(KEY_READING_REPEAT, "1"));
	}

	public int getReadingWait() {
		return Integer.parseInt(preferences.getString(KEY_READING_WAIT, "0"));
	}

	public int getUnknownMode() {
		return Integer.parseInt(preferences.getString(KEY_READING_UNKNOWN, "0"));
	}

	public int getAnnouncementMode() {
		return Integer.parseInt(preferences.getString(KEY_READING_ANNOUNCEMENT, "0"));
	}

	public int getDiscreetMode() {
		return Integer.parseInt(preferences.getString(KEY_READING_DISCREET, "0"));
	}

	public String getSharedPreferencesName() {
		return PREFERENCES_NAME;
	}
}