
package com.announcify.plugin.call.service;

import android.content.Intent;
import android.telephony.TelephonyManager;

import com.announcify.api.contact.Contact;
import com.announcify.api.contact.Formatter;
import com.announcify.api.contact.lookup.Call;
import com.announcify.api.error.ExceptionHandler;
import com.announcify.api.queue.PluginQueue;
import com.announcify.api.service.PluginService;
import com.announcify.plugin.call.receiver.RingtoneReceiver;
import com.announcify.plugin.call.util.CallnouncifySettings;

public class WorkerService extends PluginService {

    public WorkerService() {
        super("Announcify - Call");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, Thread
                .getDefaultUncaughtExceptionHandler()));

        final PluginQueue queue;

        final CallnouncifySettings settings = new CallnouncifySettings(this);

        final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        final Contact contact = new Contact(this, new Call(this), number);

        if (number == null && "".equals(number)) {
            return;
        }

        final Formatter formatter = new Formatter(this, contact, settings);

        queue = new PluginQueue("Callnouncify", PluginQueue.buildList(settings,
                formatter.format(null)), RingtoneReceiver.ACTION_START_RINGTONE, "", this);

        if (queue.isEmpty()) {
            return;
        }

        if (settings.getReadingWait() > 1000) {
            try {
                Thread.sleep(settings.getReadingWait());
            } catch (final InterruptedException e) {
            }
        }

        queue.sendToService(this, 0);
    }
}
