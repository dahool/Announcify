package com.announcify.plugin.message.mms.activity;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;

import com.announcify.api.ui.activity.PluginActivity;
import com.announcify.plugin.message.mms.R;
import com.announcify.plugin.message.mms.util.Settings;


public class SettingsActivity extends PluginActivity {

    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
        parseRingtone(requestCode, resultCode, data,
                RingtoneManager.TYPE_NOTIFICATION);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                Settings.PREFERENCES_NAME);
        getPreferenceManager().setSharedPreferencesMode(
                Context.MODE_WORLD_READABLE);

        addPreferencesFromResource(R.xml.preferences_settings);

        setCustomListeners(new Settings(this));
    }
}
