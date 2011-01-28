
package com.announcify.ui.activity;

import greendroid.widget.item.Item;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.announcify.R;
import com.announcify.api.background.sql.model.PluginModel;
import com.announcify.background.sql.AnnouncifyProvider;
import com.announcify.ui.widget.PluginItem;
import com.announcify.ui.widget.PluginItemView;
import com.announcify.ui.widget.SectionedItemAdapter;

public class AnnouncifyActivity extends BaseActivity {

    private PluginModel model;

    private ContentObserver observer;

    private SectionedItemAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarContentView(R.layout.list);

        getListView().setBackgroundColor(Color.WHITE);
        getListView().setCacheColorHint(Color.TRANSPARENT);
        getListView().setFastScrollEnabled(true);

        registerForContextMenu(getListView());

        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2,
                    final long arg3) {
                if (!((PluginItem)getListView().getItemAtPosition(arg2)).fireAction()) {
                    Toast.makeText(AnnouncifyActivity.this,
                            "The Plugin you are looking for seems to be uninstalled!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        model = new PluginModel(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshList();

        observer = new AnnouncifyObserver(new Handler());
        getContentResolver().registerContentObserver(
                Uri.withAppendedPath(AnnouncifyProvider.PROVIDER_URI, PluginModel.TABLE_NAME),
                false, observer);

        sendBroadcast(new Intent("com.announcify.ACTION_PLUGIN_CONTACT"),
                "com.announcify.PERMISSION_IM_A_PLUGIN");
    }

    private void refreshList() {
        final List<Item> items = new LinkedList<Item>();
        final Cursor cursor = model.getAll();

        while (cursor.moveToNext()) {
            items.add(new PluginItem(this, model, cursor.getInt(cursor
                    .getColumnIndex(BaseColumns._ID))));
        }

        cursor.close();

        adapter = new SectionedItemAdapter(this, items);
        getListView().setAdapter(adapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        refreshList();
    }

    private ListView getListView() {
        return (ListView)findViewById(android.R.id.list);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        final PluginItem item = (PluginItem)adapter.getItem(info.position);
        menu.setHeaderTitle(item.getName());

        getMenuInflater().inflate(R.menu.context_main, menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();

        final long id = model.getId(((PluginItemView)info.targetView).getText());

        switch (item.getItemId()) {
            case R.id.menu_uninstall:
                startActivityForResult(
                        new Intent(Intent.ACTION_DELETE, Uri.parse("package:"
                                + model.getPackage(id))), (int)(2000 + id));

                break;

            case R.id.menu_activate:
                model.togglePlugin(id);

                adapter.notifyDataSetChanged();

                break;

            case R.id.menu_report:
                final Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "Announcify - Problem with " + model.getName(id));
                sendIntent.putExtra(Intent.EXTRA_TEXT, "");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                    "tom@announcify.com"
                });
                sendIntent.setType("message/rfc822");
                startActivity(sendIntent);

                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode > 2000) {
            try {
                getPackageManager().getPackageGids(model.getPackage(requestCode - 2000));
            } catch (final NameNotFoundException e) {
                model.remove(requestCode - 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle:
                final ContentValues values = new ContentValues();
                values.put(PluginModel.KEY_PLUGIN_ACTIVE,
                        !model.getActive(model.getId("Announcify++")));
                model.getResolver().update(model.buildUri(), values, null, null);

                adapter.notifyDataSetChanged();

                break;

            case R.id.menu_help:
                startActivity(new Intent(this, HelpActivity.class));

                break;

            case R.id.menu_plugins:
                morePlugins();

                break;

            case R.id.menu_rate:
                // TODO: check URL
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.appbrain.com/app/announcify/com.announcify?install")));

                break;

            case R.id.menu_share:
                spreadTheWord();

                break;

            case R.id.menu_about:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://announcify.com/")));

                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStop() {
        getContentResolver().unregisterContentObserver(observer);

        super.onStop();
    }

    private class AnnouncifyObserver extends ContentObserver {

        public AnnouncifyObserver(final Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(final boolean selfChange) {
            super.onChange(selfChange);

            refreshList();
        }
    }
}