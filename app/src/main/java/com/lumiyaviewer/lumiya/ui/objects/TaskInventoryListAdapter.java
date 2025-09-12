package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;

public class TaskInventoryListAdapter extends BaseAdapter {
    private final Context context;
    @Nullable
    private SLTaskInventory taskInventory = null;

    public TaskInventoryListAdapter(Context context2) {
        this.context = context2;
    }

    public int getCount() {
        if (this.taskInventory != null) {
            return this.taskInventory.entries.size();
        }
        return 0;
    }

    public SLInventoryEntry getItem(int i) {
        if (this.taskInventory != null) {
            return (SLInventoryEntry) this.taskInventory.entries.get(i);
        }
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false);
        }
        SLInventoryEntry item = getItem(i);
        ((TextView) view.findViewById(R.id.itemNameTextView)).setText(item.name);
        int drawableResource = item.getDrawableResource();
        if (drawableResource >= 0) {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource);
            int subtypeDrawableResource = item.getSubtypeDrawableResource();
            if (subtypeDrawableResource >= 0) {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageResource(subtypeDrawableResource);
            } else {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null);
            }
        } else {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null);
            ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null);
        }
        view.findViewById(R.id.itemWornIcon).setVisibility(8);
        return view;
    }

    public boolean hasStableIds() {
        return false;
    }

    public void setData(SLTaskInventory sLTaskInventory) {
        this.taskInventory = sLTaskInventory;
        notifyDataSetChanged();
    }
}
