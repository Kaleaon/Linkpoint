// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import android.view.View;
import android.database.Cursor;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import android.widget.CursorAdapter;

public class InventoryListAdapter extends CursorAdapter
{
    private SLAvatarAppearance avatarAppearance;
    
    public InventoryListAdapter(final SLAvatarAppearance avatarAppearance, final Context context, final Cursor cursor) {
        super(context, cursor);
        this.avatarAppearance = avatarAppearance;
    }
    
    public void bindView(View viewById, final Context context, final Cursor cursor) {
        final SLInventoryEntry slInventoryEntry = new SLInventoryEntry(cursor);
        ((TextView)viewById.findViewById(2131755455)).setText((CharSequence)slInventoryEntry.name);
        final int drawableResource = slInventoryEntry.getDrawableResource();
        if (drawableResource >= 0) {
            ((ImageView)viewById.findViewById(2131755453)).setImageResource(drawableResource);
            final int subtypeDrawableResource = slInventoryEntry.getSubtypeDrawableResource();
            if (subtypeDrawableResource >= 0) {
                ((ImageView)viewById.findViewById(2131755454)).setImageResource(subtypeDrawableResource);
            }
            else {
                ((ImageView)viewById.findViewById(2131755454)).setImageBitmap((Bitmap)null);
            }
        }
        else {
            ((ImageView)viewById.findViewById(2131755453)).setImageBitmap((Bitmap)null);
            ((ImageView)viewById.findViewById(2131755454)).setImageBitmap((Bitmap)null);
        }
        if (this.avatarAppearance != null) {
            viewById = viewById.findViewById(2131755456);
            int visibility;
            if (this.avatarAppearance.isItemWorn(slInventoryEntry)) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
        }
        else {
            viewById.findViewById(2131755456).setVisibility(8);
        }
    }
    
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        return ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(2130968659, viewGroup, false);
    }
}
