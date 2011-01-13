
package com.announcify.plugin.call.activity;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;

import com.announcify.api.activity.PluginActivity;
import com.announcify.plugin.call.R;
import com.announcify.plugin.call.util.CallnouncifySettings;

public class Callnouncify extends PluginActivity {
    public static final String ACTION_SETTINGS = "com.announcify.plugin.call.SETTINGS";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(ACTION_SETTINGS);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preferences_settings);

        setCustomListeners(new CallnouncifySettings(this));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        parseRingtone(requestCode, resultCode, data, RingtoneManager.TYPE_RINGTONE);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
