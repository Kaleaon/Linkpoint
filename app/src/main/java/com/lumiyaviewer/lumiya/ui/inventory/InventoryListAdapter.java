package com.lumiyaviewer.lumiya.ui.inventory;
import java.util.*;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;

public class InventoryListAdapter extends CursorAdapter {
    private SLAvatarAppearance avatarAppearance;

    public InventoryListAdapter(SLAvatarAppearance sLAvatarAppearance, Context context, Cursor cursor) {
        super(context, cursor);
        this.avatarAppearance = sLAvatarAppearance;
    }

    public void bindView(View view, Context context, Cursor cursor) {
        SLInventoryEntry sLInventoryEntry = new SLInventoryEntry(cursor);
        ((TextView) view.findViewById(R.id.itemNameTextView)).setText(sLInventoryEntry.name);
        int drawableResource = sLInventoryEntry.getDrawableResource();
        if (drawableResource >= 0) {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource);
            int subtypeDrawableResource = sLInventoryEntry.getSubtypeDrawableResource();
            if (subtypeDrawableResource >= 0) {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageResource(subtypeDrawableResource);
            } else {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null);
            }
        } else {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null);
            ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null);
        }
        if (this.avatarAppearance != null) {
            view.findViewById(R.id.itemWornIcon).setVisibility(this.avatarAppearance.isItemWorn(sLInventoryEntry) ? 0 : 8);
        } else {
            view.findViewById(R.id.itemWornIcon).setVisibility(8);
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false);
    }
}
