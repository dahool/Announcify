package com.announcify.plugin.mail.service;

import java.util.LinkedList;

import android.content.Intent;
import android.util.Log;

import com.announcify.api.contact.Contact;
import com.announcify.api.contact.Lookup;
import com.announcify.api.error.ExceptionHandler;
import com.announcify.api.queue.PluginQueue;
import com.announcify.api.service.PluginService;
import com.announcify.api.text.PrepareMachine;
import com.announcify.plugin.mail.receiver.RingtoneReceiver;
import com.announcify.plugin.mail.util.MailnouncifySettings;

public class WorkerService extends PluginService {
	public static final String EXTRA_FROM = "com.announcify.plugin.mail.EXTRA_FROM";
	public static final String EXTRA_SUBJECT = "com.announcify.plugin.mail.EXTRA_SUBJECT";
	public static final String EXTRA_SNIPPET = "com.announcify.plugin.mail.EXTRA_SNIPPET";

	public WorkerService() {
		super("Announcify - Mail");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler()));

		final String address = intent.getStringExtra(EXTRA_FROM);
		Log.e("smn", address);
		final Contact contact = new Contact(this, address);
		if (address != null && !"".equals(address)) {
			Lookup.lookupMail(contact);
			Lookup.getNickname(contact);
		}

		final MailnouncifySettings settings = new MailnouncifySettings(this);

		final PrepareMachine prepare = new PrepareMachine(this, settings, contact, intent.getStringExtra(EXTRA_SUBJECT));
		final LinkedList<Object> list = prepare.prepare();

		if (list.isEmpty()) {
			return;
		}

		if (settings.getReadingWait() > 1000) {
			try {
				Thread.sleep(settings.getReadingWait());
			} catch (final InterruptedException e) {}
		}

		final PluginQueue queue = new PluginQueue("Mailnouncify", list, "", RingtoneReceiver.ACTION_STOP_RINGTONE, this);
		queue.sendToService(this, 2);
	}
}
