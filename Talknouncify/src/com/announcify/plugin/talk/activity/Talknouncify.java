package com.announcify.plugin.talk.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.announcify.activity.AnnouncifyActivity;
import com.announcify.plugin.talk.R;

public class Talknouncify extends AnnouncifyActivity {
	public static final String ACTION_SETTINGS = "com.announcify.plugin.talk.SETTINGS";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("com.announcify.plugin.message.SETTINGS");
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.preferences_settings);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		parseRingtone(requestCode, resultCode, data, RingtoneManager.TYPE_NOTIFICATION);

		super.onActivityResult(requestCode, resultCode, data);
	}
}