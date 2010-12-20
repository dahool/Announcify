package com.announcify.queue;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.announcify.handler.AnnouncificationHandler;
import com.announcify.service.ManagerService;
import com.announcify.sql.model.PluginModel;

public class Queue implements OnUtteranceCompletedListener {
	public static final String EXTRA_TEXT_SNIPPET = "com.announcify.EXTRA_TEXT_SNIPPET";

	private final Context context;
	private final AnnouncificationHandler handler;
	private final WakeLocker wakeLocker;
	private final PluginModel model;
	private final LinkedList<LittleQueue> queue;

	private boolean started;
	private boolean granted;

	public Queue(final Context context, final AnnouncificationHandler handler) {
		queue = new LinkedList<LittleQueue>();
		this.context = context;
		wakeLocker = new WakeLocker(context);
		model = new PluginModel(context);
		this.handler = handler;
	}

	public void start() {
		started = true;
		grant();
	}

	public void grant() {
		if (!started) {
			return;
		}

		granted = true;
		if (!wakeLocker.isLocked()) {
			wakeLocker.lock();
		}

		if (!queue.isEmpty()) {
			context.sendBroadcast(new Intent(queue.getFirst().getStartBroadcast()));
		}
		next();
	}

	public void deny() {
		if (!queue.isEmpty()) {
			context.sendBroadcast(new Intent(queue.getFirst().getStopBroadcast()));
		}

		granted = false;

		if (wakeLocker.isLocked()) {
			wakeLocker.unlock();
		}
	}

	public void next() {
		if (!granted) {
			return;
		}

		checkNext();

		if (!granted) {
			return;
		}

		changeLanguage();

		final Message message = Message.obtain();
		message.what = AnnouncificationHandler.WHAT_NEXT_ITEM;
		message.obj = queue.getFirst().getNext();
		handler.sendMessage(message);
	}

	private void checkNext() {
		if (queue.isEmpty()) {
			quit();
			return;
		}

		if (queue.getFirst().isEmpty()) {
			handler.sendEmptyMessage(AnnouncificationHandler.WHAT_REVERT_LOCALE);
			context.sendBroadcast(new Intent(queue.getFirst().getStopBroadcast()));
			queue.removeFirst();

			if (queue.isEmpty()) {
				quit();
				return;
			}

			if (model.getActive(queue.getFirst().getPluginName())) {
				context.sendBroadcast(new Intent(queue.getFirst().getStartBroadcast()));
				changeLanguage();
			} else {
				handler.sendEmptyMessage(AnnouncificationHandler.WHAT_REVERT_LOCALE);
				queue.removeFirst();
				checkNext();
			}
		}

		if (queue.isEmpty()) {
			quit();
		}
	}

	public void onUtteranceCompleted(final String utteranceId) {
		next();
	}

	public void putLast(final LittleQueue little) {
		queue.add(little);

		if (!granted) {
			next();
		}
	}

	public void putFirst(final LittleQueue little) {
		queue.add(0, little);
		// TODO: if (grant) ?
		grant();
	}

	private void changeLanguage() {
		final Message msg = Message.obtain();
		msg.what = AnnouncificationHandler.WHAT_CHANGE_LOCALE;
		handler.sendMessage(msg);
	}

	public void quit() {
		model.close();

		if (wakeLocker.isLocked()) {
			wakeLocker.unlock();
		}

		deny();

		context.stopService(new Intent(context, ManagerService.class));
	}
}