package com.announcify.plugin.message.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.announcify.activity.AnnouncifyActivity;
import com.announcify.plugin.message.R;

public class Messnouncify extends AnnouncifyActivity {
	public static final String ACTION_SETTINGS = "com.announcify.plugin.message.SETTINGS";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("com.announcify.plugin.message.SETTINGS");
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.preferences_settings);

		final Cursor message = getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://mms-sms/"), "conversations"), null, null, null, null);
		message.moveToFirst();

		for (final String s : message.getColumnNames()) {
			try {
				Log.e("SayMyName", s);
				Log.e("SayMyName", message.getString(message.getColumnIndex(s)));
			} catch (final Exception e) {
				e.printStackTrace();
				Log.e("SayMyName", "ERROR");
			}
		}

		message.close();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		parseRingtone(requestCode, resultCode, data, RingtoneManager.TYPE_NOTIFICATION);

		super.onActivityResult(requestCode, resultCode, data);
	}
}