package com.announcify.receiver.gravity;

import android.content.Context;

import com.announcify.receiver.gravity.motion.Motion;
import com.announcify.receiver.gravity.motion.MotionListener;

public class GravityListener {

	public interface GravityStateListener {

		public void onDisplayUp();

		public void onDisplayDown();

	}

	private final int DISPLAY_UP = 0;

	private final int DISPLAY_DOWN = 1;

	private final GravityStateListener listener;

	private int lastMotionEvent = -1;

	public boolean isFlat = false;

	public boolean isDisplayUp = false;

	public boolean isDisplayDown = false;

	public boolean isScreenOn = true;

	private final Motion motion;

	public GravityListener(final Context context, final GravityStateListener listener) {
		this.listener = listener;

		// INITIALIZE MOTION LISTENER
		motion = new Motion(context, new MotionListener() {

			public void onDisplayUp() {
				if (lastMotionEvent != DISPLAY_UP) {
					lastMotionEvent = DISPLAY_UP;
					GravityListener.this.listener.onDisplayUp();
				}
				isFlat = true;
				isDisplayUp = true;
				isDisplayDown = false;
			}

			public void onDisplayDown() {
				if (lastMotionEvent != DISPLAY_DOWN) {
					lastMotionEvent = DISPLAY_DOWN;
					GravityListener.this.listener.onDisplayDown();
				}
				isFlat = true;
				isDisplayUp = false;
				isDisplayDown = true;
			}
		});
	}

	public void setAccuracy(final float accuracy) {
		motion.accuracy = accuracy;
	}

	public float getAccuracy() {
		return motion.accuracy;
	}

	public void stopAccelerometer() {
		motion.stop();
	}

	public void resumeAccelerometer() {
		motion.resume();
	}
}
