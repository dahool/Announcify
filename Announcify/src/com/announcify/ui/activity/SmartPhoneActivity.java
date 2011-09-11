package com.announcify.ui.activity;

import org.mailboxer.saymyname.R;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import at.bartinger.toastplugin.SmartPhonePluginHelper;

import com.announcify.api.background.util.PluginSettings;
import com.announcify.api.ui.activity.PluginActivity;

public class SmartPhoneActivity extends PluginActivity {

	private CheckBoxPreference announcifyCheck;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, new PluginSettings(this, "com.announcify.locale") {

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

		getPreferenceManager().setSharedPreferencesName("com.announcify.locale");
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);

		announcifyCheck = (CheckBoxPreference) findPreference("preference_locale_enabled");

		String data = SmartPhonePluginHelper.getData(this);
		announcifyCheck.setChecked(Boolean.parseBoolean(data));
	}

	@Override
	public void finish() {
		SmartPhonePluginHelper.setResultAndFinish(this, "Announcify", "Announcifications " + new Boolean(announcifyCheck.isChecked()).toString(), new Boolean(announcifyCheck.isChecked()).toString());
	}
}
