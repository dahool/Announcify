package com.announcify.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.announcify.R;
import com.announcify.activity.chooser.GroupChooser;
import com.announcify.util.AnnouncifySecurity;
import com.announcify.util.AnnouncifySettings;

public class SettingsActivity extends PreferenceActivity {
	private AlertDialog dialog;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getBooleanExtra(AnnouncifySecurity.EXTRA_LICENSED, false)) {
			finish();
			return;
		}

		getPreferenceManager().setSharedPreferencesName(AnnouncifySettings.PREFERENCES_NAME);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.preferences_settings);

		getPreferenceScreen().findPreference("preference_tts_settings").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				final Intent intentTTS = new Intent();
				intentTTS.setComponent(new ComponentName("com.android.settings", "com.android.settings.TextToSpeechSettings"));
				startActivity(intentTTS);

				return false;
			}
		});

		getPreferenceScreen().findPreference("preference_choose_group").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(SettingsActivity.this, GroupChooser.class));

				return false;
			}
		});
	}

	// Nags user until he finally donates. :P
	// @Override
	// public boolean dispatchKeyEvent(final KeyEvent event) {
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// if (dialog != null) dialog.dismiss();
	// finish();
	// return false;
	// }
	//
	// return nagUser();
	// }
	//
	// @Override
	// public boolean dispatchTouchEvent(final MotionEvent ev) {
	// return nagUser();
	// }
	//
	// @Override
	// public boolean dispatchTrackballEvent(final MotionEvent ev) {
	// return nagUser();
	// }
	//
	// private boolean nagUser() {
	// // if (Money.isPaid(this)) {
	// // return false;
	// // } else {
	// // if (dialog == null) {
	// // Builder builder = new AlertDialog.Builder(this);
	// // builder.setTitle("Please upgrade to Pro Version");
	// //
	// builder.setMessage("These settings are only available for Pro users, sorry! Please buy the Pro Version from Android Market.");
	// // dialog = builder.create();
	// // dialog.show();
	// // } else {
	// // if (!dialog.isShowing()) {
	// // dialog.show();
	// // }
	// // }
	// //
	// // return true;
	// // }
	// return false;
	// }
}