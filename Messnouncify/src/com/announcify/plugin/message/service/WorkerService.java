package com.announcify.plugin.message.service;

import java.util.LinkedList;

import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.announcify.contact.Contact;
import com.announcify.contact.Lookup;
import com.announcify.plugin.message.receiver.RingtoneReceiver;
import com.announcify.plugin.message.util.MessnouncifySettings;
import com.announcify.queue.LittleQueue;
import com.announcify.queue.Prepare;
import com.announcify.service.AnnouncifyService;

public class WorkerService extends AnnouncifyService {

	public WorkerService() {
		super("Announcify - Message");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final String message;
		final String number;

		if (intent.getExtras().containsKey("pdus")) {
			final Object[] pdusObj = (Object[]) intent.getExtras().get("pdus");

			final SmsMessage[] messages = new SmsMessage[pdusObj.length];
			for (int i = 0; i < pdusObj.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}

			String temp = "";

			if (messages.length > 1) {
				for (final SmsMessage currentMessage : messages) {
					temp = temp + currentMessage.getDisplayMessageBody() + '\n';
				}
			} else {
				temp = messages[0].getDisplayMessageBody();
			}

			number = messages[0].getDisplayOriginatingAddress();
			message = temp;
		} else {
			message = "No message";
			number = intent.getStringExtra("com.announcify.EXTRA_TEST");
		}

		Log.e("smn", number);
		final Contact contact = new Contact(this, number);
		if (number != null && !"".equals(number)) {
			Lookup.lookupNumber(contact);
			Lookup.getNickname(contact);
		}

		MessnouncifySettings settings = new MessnouncifySettings(this);

		Prepare prepare = new Prepare(this, settings, contact, message);
		final LinkedList<Object> list = new LinkedList<Object>();
		prepare.getQueue(list);

		if (list.isEmpty()) return;

		final LittleQueue queue = new LittleQueue("Messnouncify", list, "", RingtoneReceiver.ACTION_STOP_RINGTONE, this);
		queue.sendToService(this, 1);
	}
}
