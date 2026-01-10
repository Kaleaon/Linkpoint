package com.linkpoint.ui.inventory
import java.util.*

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.modules.SLAvatarAppearance

class InventoryListAdapter : CursorAdapter {
    private SLAvatarAppearance avatarAppearance

    InventoryListAdapter(SLAvatarAppearance sLAvatarAppearance, Context context, Cursor cursor) {
        super(context, cursor)
        this.avatarAppearance = sLAvatarAppearance
    }

    fun bindView(View view, Context context, Cursor cursor)  {
        SLInventoryEntry sLInventoryEntry = SLInventoryEntry(cursor)
        ((view as TextView).findViewById(R.id.itemNameTextView)).setText(sLInventoryEntry.name)
        var drawableResource: Int = sLInventoryEntry.getDrawableResource()
        if (drawableResource >= 0) {
            ((view as ImageView).findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource)
            var subtypeDrawableResource: Int = sLInventoryEntry.getSubtypeDrawableResource()
            if (subtypeDrawableResource >= 0) {
                ((view as ImageView).findViewById(R.id.itemSubTypeIconView)).setImageResource(subtypeDrawableResource)
            } else {
                ((view as ImageView).findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
            }
        } else {
            ((view as ImageView).findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null)
            ((view as ImageView).findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
        }
        if (this.avatarAppearance != null) {
            view.findViewById(R.id.itemWornIcon).setVisibility(this.avatarAppearance.isItemWorn(sLInventoryEntry) ? 0 : 8)
        } else {
            view.findViewById(R.id.itemWornIcon).setVisibility(8)
        }
    }

    fun newView(Context context, Cursor cursor, ViewGroup viewGroup): View {
        return ((context as LayoutInflater).getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false)
    }
}
