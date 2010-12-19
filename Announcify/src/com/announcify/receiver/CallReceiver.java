package com.announcify.receiver;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.test.IsolatedContext;
import android.util.Log;

import com.announcify.activity.control.RemoteControlDialog;

public class CallReceiver extends PhoneStateListener {
	private final Context context;
	private boolean stickyBroadcastReceived;

	public CallReceiver(final Context context) {
		this.context = context;
	}

	@Override
	public void onCallStateChanged(final int state, final String incomingNumber) {
		if (state != TelephonyManager.CALL_STATE_RINGING) {
			if (!stickyBroadcastReceived) {
				stickyBroadcastReceived = true;
				return;
			}

			context.sendBroadcast(new Intent(RemoteControlDialog.ACTION_PAUSE));
		}
	}
	
	public void setOnCall(boolean onCall) {
		this.stickyBroadcastReceived = onCall;
	}
}