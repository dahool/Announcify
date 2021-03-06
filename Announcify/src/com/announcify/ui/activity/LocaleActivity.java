package com.announcify.ui.activity;

import org.mailboxer.saymyname.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;

import com.announcify.api.background.util.PluginSettings;
import com.announcify.api.ui.activity.PluginActivity;
import com.announcify.background.receiver.LocaleReceiver;

public class LocaleActivity extends PluginActivity {

	private final boolean isCancelled = false;

	private CheckBoxPreference announcifyCheck;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, new PluginSettings(this,
				"com.announcify.locale") {

			@Override
			public String getSettingsAction() {
				return "";
			}

			@Override
			public int getPriority() {
				return 9;
			}

			@Override
			public String getEventType() {
				return "Locale";
			}
		}, R.xml.preferences_locale_settings);

		getPreferenceManager()
				.setSharedPreferencesName("com.announcify.locale");
		getPreferenceManager().setSharedPreferencesMode(
				Context.MODE_WORLD_READABLE);

		announcifyCheck = (CheckBoxPreference) findPreference("preference_locale_enabled");

		if (savedInstanceState == null) {
			announcifyCheck.setChecked(getIntent().getBooleanExtra(
					LocaleReceiver.ANNOUNCIFY_ENABLED, true));
		}
	}

	@Override
	public void finish() {
		if (isCancelled) {
			setResult(RESULT_CANCELED);
		} else {
			// This is the store-and-forward Intent to ourselves.
			final Intent returnIntent = new Intent();

			// this extra is the data to ourselves: either for the Activity or
			// the BroadcastReceiver
			returnIntent.putExtra(LocaleReceiver.ANNOUNCIFY_ENABLED,
					announcifyCheck.isChecked());

			String blurb;
			if (announcifyCheck.isChecked()) {
				blurb = "Announcify" + " " + getString(R.string.string_enabled);
			} else {
				blurb = "Announcify" + " "
						+ getString(R.string.string_disabled);
			}

			// this is the blurb shown in the Locale UI
			if (blurb.length() > 40) {
				returnIntent.putExtra(
						"com.twofortyfouram.locale.intent.extra.BLURB",
						blurb.substring(0, 40));
			} else {
				returnIntent.putExtra(
						"com.twofortyfouram.locale.intent.extra.BLURB", blurb);
			}

			setResult(RESULT_OK, returnIntent);
		}

		super.finish();
	}
}
