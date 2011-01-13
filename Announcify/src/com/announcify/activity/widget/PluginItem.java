
package com.announcify.activity.widget;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.announcify.R;
import com.announcify.api.sql.model.PluginModel;

public class PluginItem extends Item {
    private final int id;

    private final Context context;

    private final PluginModel model;

    public String header;

    public PluginItem(final Context context, final int id) {
        this.id = id;
        this.context = context;

        model = new PluginModel(context);
    }

    @Override
    public ItemView newView(final Context context, final ViewGroup parent) {
        return createCellFromXml(context, R.layout.list_item_plugin, parent);
    }

    public boolean getActive() {
        return model.getActive(id);
    }

    public void toggle() {
        model.togglePlugin(id);
    }

    public String getName() {
        return model.getName(id);
    }

    public Drawable getDrawable() throws NameNotFoundException {
        return context.getPackageManager().getActivityIcon(new Intent(getAction()));
    }

    public int getPriority() {
        return model.getPriority(id);
    }

    public String getAction() {
        return model.getAction(id);
    }

    public boolean getBroadcast() {
        return model.getBroadcast(id);
    }

    public void fireAction() {
        try {
            final Intent intent = new Intent(getAction());
            if (getBroadcast()) {
                context.sendBroadcast(intent);
            } else {
                context.startActivity(intent);
            }
        } catch (final Exception e) {
            e.printStackTrace();

            model.remove(id);
            // TODO: notify data set changed
        }
    }
}
