package com.announcify.plugin.talk.google.service;

import java.util.LinkedList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.announcify.api.background.error.ExceptionHandler;
import com.announcify.api.background.service.PluginService;
import com.announcify.plugin.talk.google.util.Settings;

public class TalkService extends Service {

	private class TalkObserver extends ContentObserver {

		private boolean paused;

		private final Handler handler;

		public TalkObserver(final Handler handler) {
			super(handler);

			this.handler = handler;
		}

		@Override
		public void onChange(final boolean selfChange) {
			if (paused) {
				return;
			}

			Cursor message = null;
			Cursor conversation = null;
			Cursor contact = null;
			try {
				final String[] messageProjection = new String[] { "body",
						"date", "type" };
				message = getContentResolver()
						.query(Uri.withAppendedPath(
								Uri.parse("content://com.google.android.providers.talk/"),
								"messages"), messageProjection, "err_code = 0",
								null, "date DESC");
				if (!message.moveToFirst()) {
					return;
				}

				if (message
						.getInt(message.getColumnIndex(messageProjection[2])) != 1) {
					return;
				}

				final String[] conversationProjection = new String[] {
						"last_unread_message", "last_message_date" };
				conversation = getContentResolver()
						.query(Uri.withAppendedPath(
								Uri.parse("content://com.google.android.providers.talk/"),
								"chats"), conversationProjection, null, null,
								"last_message_date DESC");
				if (!conversation.moveToFirst()) {
					return;
				}

				final String[] contactProjection = new String[] { "username" };
				contact = getContentResolver()
						.query(Uri
								.withAppendedPath(
										Uri.parse("content://com.google.android.providers.talk/"),
										"contacts"),
								contactProjection,
								"last_message_date = "
										+ conversation.getLong(conversation
												.getColumnIndex("last_message_date")),
								null, null);
				if (!contact.moveToFirst()) {
					return;
				}

				final String username = contact.getString(contact
						.getColumnIndex(contactProjection[0]));

				final Intent intent = new Intent(TalkService.this,
						WorkerService.class);
				intent.setAction(PluginService.ACTION_ANNOUNCE);
				intent.putExtra(WorkerService.EXTRA_FROM, username);
				intent.putExtra(WorkerService.EXTRA_MESSAGE,
						message.getString(message
								.getColumnIndex(messageProjection[0])));
				startService(intent);

				paused = true;
				handler.postDelayed(new Runnable() {

					public void run() {
						paused = false;
					}
				}, settings.getShutUp());
			} finally {
				if (contact != null) {
					contact.close();
				}
				if (conversation != null) {
					conversation.close();
				}
				if (message != null) {
					message.close();
				}
			}
		}
	}

	private LinkedList<String> addresses;

	private HandlerThread thread;
	private TalkObserver observer;
	private Settings settings;

	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(
				getBaseContext()));

		thread = new HandlerThread("TalkThread");
		thread.start();

		settings = new Settings(this);

		final Handler handler = new Handler(thread.getLooper());
		handler.post(new Runnable() {

			public void run() {
				Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(
						getBaseContext()));
			}
		});

		addresses = new LinkedList<String>();

		final AccountManager manager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
		final Account[] accounts = manager.getAccountsByType("com.google");

		if (accounts.length == 0) {
			stopSelf();
		}

		for (final Account account : accounts) {
			addresses.add(account.name);
		}

		observer = new TalkObserver(handler);

		getContentResolver().registerContentObserver(
				Uri.withAppendedPath(Uri
						.parse("content://com.google.android.providers.talk/"),
						"messages"), true, observer);
	}

	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(observer);

		thread.getLooper().quit();

		super.onDestroy();
	}
}
