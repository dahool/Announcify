package com.announcify.plugin.voice.call.service;

import android.content.Intent;
import android.telephony.TelephonyManager;

import com.announcify.api.AnnouncifyIntent;
import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.ContactFilter;
import com.announcify.api.background.error.ExceptionHandler;
import com.announcify.api.background.service.PluginService;
import com.announcify.api.background.text.Formatter;
import com.announcify.plugin.voice.call.util.Settings;

public class WorkerService extends PluginService {

	public final static String ACTION_START_RINGTONE = "com.announcify.plugin.voice.call.ACTION_START_RINGTONE";
	public final static String ACTION_STOP_RINGTONE = "com.announcify.plugin.voice.call.ACTION_STOP_RINGTONE";

	public WorkerService() {
		super("Announcify - Call", ACTION_START_RINGTONE, ACTION_STOP_RINGTONE);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(
				getBaseContext()));

		if (settings == null) {
			settings = new Settings(this);
		}

		if (ACTION_ANNOUNCE.equals(intent.getAction())) {
			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			if (number == null) {
				number = "";
			}

			final Contact contact = new Contact(this,
					new com.announcify.api.background.contact.lookup.Number(
							this), number);

			if (!settings.isChuckNorris()) {
				if (!ContactFilter.announcableContact(this, contact)) {
					playRingtone();
					return;
				}
			}

			final Formatter formatter = new Formatter(this, contact, settings);

			final AnnouncifyIntent announcify = new AnnouncifyIntent(this,
					settings);
			announcify.setStartBroadcast(ACTION_START_RINGTONE);
			announcify.setStopBroadcast(ACTION_STOP_RINGTONE);
			announcify.announce(formatter.format(""));
		} else {
			super.onHandleIntent(intent);
		}
	}
}
