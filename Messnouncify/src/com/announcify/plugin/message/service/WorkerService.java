
package com.announcify.plugin.message.service;

import android.content.Intent;
import android.telephony.SmsMessage;

import com.announcify.api.contact.Contact;
import com.announcify.api.contact.Formatter;
import com.announcify.api.contact.lookup.Call;
import com.announcify.api.error.ExceptionHandler;
import com.announcify.api.queue.PluginQueue;
import com.announcify.api.service.PluginService;
import com.announcify.plugin.message.receiver.RingtoneReceiver;
import com.announcify.plugin.message.util.MessnouncifySettings;

public class WorkerService extends PluginService {

    public WorkerService() {
        super("Announcify - Message");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, Thread
                .getDefaultUncaughtExceptionHandler()));

        final String message;
        final String number;

        if (intent.getExtras().containsKey("pdus")) {
            final Object[] pdusObj = (Object[])intent.getExtras().get("pdus");

            final SmsMessage[] messages = new SmsMessage[pdusObj.length];
            for (int i = 0; i < pdusObj.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])pdusObj[i]);
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
            message = "Please spread the word about Announcify";
            number = intent.getStringExtra("com.announcify.EXTRA_TEST");
        }

        final PluginQueue queue;

        final MessnouncifySettings settings = new MessnouncifySettings(this);

        final Contact contact = new Contact(this, new Call(this), number);
        if (number == null && "".equals(number)) {
            return;
        }

        final Formatter formatter = new Formatter(this, contact, settings);

        queue = new PluginQueue("Messnouncify", PluginQueue.buildList(settings,
                formatter.format(message)), "", RingtoneReceiver.ACTION_STOP_RINGTONE, this);

        if (queue.isEmpty()) {
            return;
        }

        if (settings.getReadingWait() > 1000) {
            try {
                Thread.sleep(settings.getReadingWait());
            } catch (final InterruptedException e) {
            }
        }

        queue.sendToService(this, 1);
    }
}
