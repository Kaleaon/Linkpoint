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

class InventoryListAdapter : CursorAdapter() {
    private SLAvatarAppearance avatarAppearance

    public InventoryListAdapter(SLAvatarAppearance sLAvatarAppearance, Context context, Cursor cursor) {
        super(context, cursor)
        this.avatarAppearance = sLAvatarAppearance
    }

    fun bindView(view: View, context: Context, cursor: Cursor) {
        val sLInventoryEntry: SLInventoryEntry = SLInventoryEntry(cursor)
        ((TextView) view.findViewById(R.id.itemNameTextView)).setText(sLInventoryEntry.name)
        val drawableResource: Int = sLInventoryEntry.getDrawableResource()
        if (drawableResource >= 0) {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource)
            val subtypeDrawableResource: Int = sLInventoryEntry.getSubtypeDrawableResource()
            if (subtypeDrawableResource >= 0) {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageResource(subtypeDrawableResource)
            } else {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
            }
        } else {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null)
            ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
        }
        if (this.avatarAppearance != null) {
            view.findViewById(R.id.itemWornIcon).setVisibility(this.avatarAppearance.isItemWorn(sLInventoryEntry) ? 0 : 8)
        } else {
            view.findViewById(R.id.itemWornIcon).setVisibility(8)
        }
    }

     public fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View {
        return ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false)
    }
}
