package com.announcify.ui.control;

import org.mailboxer.saymyname.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RemoteControlDialogInstaller extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent shortcutIntent = new Intent(this,
				RemoteControlDialog.class);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		final Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.control_title));
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.launcher_icon));

		setResult(RESULT_OK, intent);

		finish();
	}
}
