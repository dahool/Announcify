package com.announcify.plugin.message.mms.service;

import android.content.Intent;

import com.announcify.api.AnnouncifyIntent;
import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.ContactFilter;
import com.announcify.api.background.error.ExceptionHandler;
import com.announcify.api.background.service.PluginService;
import com.announcify.api.background.text.Formatter;
import com.announcify.plugin.message.mms.util.Settings;
import com.announcify.plugin.message.mms.util.pdu.EncodedStringValue;
import com.announcify.plugin.message.mms.util.pdu.PduHeaders;
import com.announcify.plugin.message.mms.util.pdu.PduParser;

public class WorkerService extends PluginService {

	public static final String ACTION_START_RINGTONE = "com.announcify.plugin.message.mms.ACTION_START_RINGTONE";
	public static final String ACTION_STOP_RINGTONE = "com.announcify.plugin.message.mms.ACTION_STOP_RINGTONE";

	public WorkerService() {
		super("Announcify - Multimedia Message", ACTION_START_RINGTONE,
				ACTION_STOP_RINGTONE);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(
				getBaseContext()));

		if (settings == null) {
			settings = new Settings(this);
		}

		if (ACTION_ANNOUNCE.equals(intent.getAction())) {
			String number = null;

			if (intent.getExtras().containsKey("data")) {
				final PduParser parser = new PduParser();
				final PduHeaders headers = parser.parseHeaders(intent
						.getByteArrayExtra("data"));

				if (headers == null) {
					playRingtone();
					return;
				}

				if (headers.getMessageType() == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) {
					final EncodedStringValue from = headers.getFrom();
					if (from != null) {
						number = from.getString();
					} else {
						number = "";
					}
				}
			} else {
				number = intent.getStringExtra("com.announcify.EXTRA_TEST");
			}

			final Settings settings = new Settings(this);
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
			announcify.setStopBroadcast(ACTION_START_RINGTONE);
			announcify.announce(formatter.format(settings.getEventType()));
		} else {
			super.onHandleIntent(intent);
		}
	}
}
