package com.announcify.plugin.call.service;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.announcify.contact.Contact;
import com.announcify.contact.Lookup;
import com.announcify.contact.Prepare;
import com.announcify.plugin.call.receiver.RingtoneReceiver;
import com.announcify.plugin.call.util.CallnouncifySettings;
import com.announcify.queue.LittleQueue;
import com.announcify.service.AnnouncifyService;
import com.announcify.util.AnnouncifySettings;
import com.announcify.util.HeadsetFinder;

public class WorkerService extends AnnouncifyService {

	public WorkerService() {
		super("Announcify - Call");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		Log.e("smn", number);
		final Contact contact = new Contact(this, number);
		if (number != null && !"".equals(number)) {
			Lookup.lookupNumber(contact);
			Lookup.getNickname(contact);
		}

		CallnouncifySettings settings = new CallnouncifySettings(this);
		
		Prepare prepare = new Prepare(this, settings, contact, "");
		final LinkedList<Object> list = new LinkedList<Object>();
		prepare.getQueue(list);

		final LittleQueue queue = new LittleQueue("Callnouncify", list, RingtoneReceiver.ACTION_START_RINGTONE, RingtoneReceiver.ACTION_STOP_RINGTONE, this);
		queue.sendToService(this, 0);
	}
}
