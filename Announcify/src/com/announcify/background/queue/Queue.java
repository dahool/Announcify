package com.announcify.background.queue;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.announcify.api.background.queue.PluginQueue;
import com.announcify.background.handler.AnnouncificationHandler;


public class Queue implements OnUtteranceCompletedListener {

    public static final String EXTRA_TEXT_SNIPPET = "com.announcify.EXTRA_TEXT_SNIPPET";

    private final Context context;

    private final AnnouncificationHandler handler;

    private final WakeLocker wakeLocker;

    private final LinkedList<PluginQueue> queue;

    private boolean started;

    private boolean granted;

    public Queue(final Context context, final AnnouncificationHandler handler) {
        queue = new LinkedList<PluginQueue>();
        this.context = context;
        wakeLocker = new WakeLocker(context);
        this.handler = handler;
    }

    private void changeLanguage() {
        final Message msg = Message.obtain();
        msg.what = AnnouncificationHandler.WHAT_CHANGE_LOCALE;
        handler.sendMessage(msg);
    }

    private void checkNext() {
        if (queue.isEmpty()) {
            quit();
            return;
        }

        if (queue.getFirst().isEmpty()) {
            handler.sendEmptyMessage(AnnouncificationHandler.WHAT_REVERT_LOCALE);
            context.sendBroadcast(new Intent(queue.getFirst()
                    .getStopBroadcast()));
            queue.removeFirst();

            if (queue.isEmpty()) {
                quit();
                return;
            }

            context.sendBroadcast(new Intent(queue.getFirst()
                    .getStartBroadcast()));
            changeLanguage();
        }

        if (queue.isEmpty()) {
            quit();
            return;
        }
    }

    public void deny() {
        if (!queue.isEmpty()) {
            context.sendBroadcast(new Intent(queue.getFirst()
                    .getStopBroadcast()));
        }

        granted = false;

        if (wakeLocker.isLocked()) {
            wakeLocker.unlock();
        }
    }

    public void grant() {
        if (!started) return;

        granted = true;
        if (!wakeLocker.isLocked()) {
            wakeLocker.lock();
        }

        if (!queue.isEmpty()) {
            context.sendBroadcast(new Intent(queue.getFirst()
                    .getStartBroadcast()));
        }
        next();
    }

    public void next() {
        if (!granted) return;

        checkNext();

        if (!granted) return;

        changeLanguage();

        final Message message = Message.obtain();
        message.what = AnnouncificationHandler.WHAT_NEXT_ITEM;
        message.obj = queue.getFirst().getNext();
        handler.sendMessage(message);
    }

    public void onUtteranceCompleted(final String utteranceId) {
        next();
    }

    public void putFirst(final PluginQueue little) {
        queue.add(0, little);
        // TODO: if (grant) ?
        grant();
    }

    public void putLast(final PluginQueue little) {
        queue.add(little);

        if (!granted) {
            next();
        }
    }

    public void quit() {
        if (wakeLocker.isLocked()) {
            wakeLocker.unlock();
        }

        deny();

        handler.sendEmptyMessage(AnnouncificationHandler.WHAT_SHUTDOWN);
    }

    public void start() {
        started = true;
        grant();
    }
}
