package com.lumiyaviewer.lumiya.ui.objects
import java.util.*

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory

class TaskInventoryListAdapter : BaseAdapter {
    private Context context
    @Nullable
    private SLTaskInventory taskInventory = null

    TaskInventoryListAdapter(Context context2) {
        this.context = context2
    }

    Int getCount() {
        if (this.taskInventory != null) {
            return this.taskInventory.entries.size()
        }
        return 0
    }

    SLInventoryEntry getItem(Int i) {
        if (this.taskInventory != null) {
            return (SLInventoryEntry) this.taskInventory.entries.get(i)
        }
        return null
    }

    Long getItemId(Int i) {
        return (Long) i
    }

    View getView(Int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false)
        }
        SLInventoryEntry item = getItem(i)
        ((TextView) view.findViewById(R.id.itemNameTextView)).setText(item.name)
        Int drawableResource = item.getDrawableResource()
        if (drawableResource >= 0) {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource)
            Int subtypeDrawableResource = item.getSubtypeDrawableResource()
            if (subtypeDrawableResource >= 0) {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageResource(subtypeDrawableResource)
            } else {
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
            }
        } else {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null)
            ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
        }
        view.findViewById(R.id.itemWornIcon).setVisibility(8)
        return view
    }

    Boolean hasStableIds() {
        return false
    }

    Unit setData(SLTaskInventory sLTaskInventory) {
        this.taskInventory = sLTaskInventory
        notifyDataSetChanged()
    }
}
