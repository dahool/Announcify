
package com.announcify.plugin.mail.google.activity;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;

import com.announcify.api.ui.activity.PluginActivity;
import com.announcify.plugin.mail.google.R;
import com.announcify.plugin.mail.google.service.MailService;
import com.announcify.plugin.mail.google.util.Settings;

public class SettingsActivity extends PluginActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(Settings.PREFERENCES_NAME);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);

        addPreferencesFromResource(R.xml.preferences_mail_settings);
        addPreferencesFromResource(R.xml.preferences_settings);

        setCustomListeners(new Settings(this));
    }

    @Override
    protected void onPause() {
        final Intent serviceIntent = new Intent(this, MailService.class);
        stopService(serviceIntent);
        startService(serviceIntent);

        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        parseRingtone(requestCode, resultCode, data, RingtoneManager.TYPE_NOTIFICATION);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
