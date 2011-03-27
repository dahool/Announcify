package com.announcify.plugin.mail.k9.contact;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;

import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.lookup.LookupMethod;

public class Mail implements LookupMethod {

    public static void prepareAddress(final Contact contact, final boolean cheat) {
        if (contact.getAddress().contains("\"") && cheat) {
            contact.setFullname(contact.getAddress().substring(1, contact.getAddress().lastIndexOf("\"")));

            contact.setLookupString("com.announcify");
            // little hack to avoid announcing this contact as unknown, if he's
            // not in addressbook!
        }

        if (contact.getAddress().contains("<")) {
            contact.setAddress(contact.getAddress().substring(contact.getAddress().indexOf('<') + 1, contact.getAddress().indexOf('>')));
        }
    }

    private final Context context;
    private Contact contact;

    private final boolean cheat;

    public Mail(final Context context, final boolean cheat) {
        this.context = context;
        this.cheat = cheat;
    }

    public void getAddress() {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(Email.CONTENT_URI, new String[] { Email.DATA1 }, Contacts.LOOKUP_KEY + " = ?", new String[] { contact.getLookupString() }, null);
            if (!cursor.moveToFirst()) {
                return;
            }

            // TODO: implement?
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void getLookup(final Contact contact) {
        this.contact = contact;

        prepareAddress(contact, cheat);

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, contact.getAddress()), new String[] { Email.LOOKUP_KEY }, null, null, null);
            if (!cursor.moveToFirst()) {
                return;
            }

            contact.setLookupString(cursor.getString(0));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void getType() {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(Email.CONTENT_URI, new String[] { Email.LABEL, Email.TYPE }, Email.LOOKUP_KEY + " = ? AND " + Email.DATA1 + " = ?", new String[] { contact.getLookupString(), contact.getAddress() }, null);
            if (!cursor.moveToFirst()) {
                return;
            }

            String label = cursor.getString(cursor.getColumnIndex(Email.LABEL));
            if (label == null) {
                label = Resources.getSystem().getStringArray(android.R.array.emailAddressTypes)[cursor.getInt(cursor.getColumnIndex(Email.TYPE)) - 1];
            }
            contact.setType(label);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
