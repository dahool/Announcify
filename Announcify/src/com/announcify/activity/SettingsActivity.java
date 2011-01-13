
package com.announcify.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.announcify.R;
import com.announcify.api.util.AnnouncifySettings;
import com.announcify.util.AnnouncifySecurity;

public class SettingsActivity extends PreferenceActivity {
    private AnnouncifySecurity security;

    private Thread thread;

    private boolean licensed;

    private boolean started;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(AnnouncifySettings.PREFERENCES_NAME);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preferences_settings);

        try {
            createPackageContext("com.announcify.paid", 0);

            showDialog(1);

            thread = new Thread() {
                @Override
                public void run() {
                    security = new AnnouncifySecurity(SettingsActivity.this);
                }
            };
            thread.start();

            getPreferenceScreen().findPreference("preference_tts_settings")
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        public boolean onPreferenceClick(final Preference preference) {
                            final Intent intentTTS = new Intent();
                            intentTTS.setComponent(new ComponentName("com.android.settings",
                                    "com.android.settings.TextToSpeechSettings"));
                            startActivity(intentTTS);

                            return false;
                        }
                    });

            getPreferenceScreen().findPreference("preference_choose_group")
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        public boolean onPreferenceClick(final Preference preference) {
                            // startActivity(new Intent(SettingsActivity.this,
                            // GroupChooser.class));

                            return false;
                        }
                    });
        } catch (final Exception e) {
            licensed = false;
        }
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case 0:
                return new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(R.string.unlicensed_dialog_title)
                        .setMessage(R.string.unlicensed_dialog_body)
                        .setPositiveButton(R.string.buy_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog,
                                            final int which) {
                                        final Intent marketIntent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://market.android.com/details?id="
                                                        + getPackageName()));
                                        startActivity(marketIntent);
                                    }
                                })
                        .setNegativeButton(R.string.quit_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog,
                                            final int which) {
                                        finish();
                                    }
                                }).create();

            case 1:
                return ProgressDialog.show(this, "Announcify",
                        "Verifying if you really bought Pro...", true);

            case 2:
                started = true;
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return !licensed;
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        return !licensed;
    }

    @Override
    protected void onPause() {
        if (security != null) {
            licensed = security.isLicensed();
            security.quit();
        } else {
            licensed = false;
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (started && !licensed) {
            showDialog(0);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (security != null) {
            security.quit();
        }

        super.onDestroy();
    }
}
