package com.linkpoint.ui.objects
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
import com.linkpoint.R
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.inventory.SLTaskInventory

class TaskInventoryListAdapter : BaseAdapter() {
    private val Context context
    private SLTaskInventory taskInventory = null

    public TaskInventoryListAdapter(Context context2) {
        this.context = context2
    }

     public fun getCount(): Int {
        if (this.taskInventory != null) {
            return this.taskInventory.entries.size()
        }
        return 0
    }

     public fun getItem(i: Int): SLInventoryEntry {
        if (this.taskInventory != null) {
            return (SLInventoryEntry) this.taskInventory.entries.get(i)
        }
        return null
    }

     public fun getItemId(i: Int): Long {
        return (Long) i
    }

     public fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false)
        }
        val item: SLInventoryEntry = getItem(i)
        ((TextView) view.findViewById(R.id.itemNameTextView)).setText(item.name)
        val drawableResource: Int = item.getDrawableResource()
        if (drawableResource >= 0) {
            ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource)
            val subtypeDrawableResource: Int = item.getSubtypeDrawableResource()
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

     public fun hasStableIds(): Boolean {
        return false
    }

    fun setData(sLTaskInventory: SLTaskInventory) {
        this.taskInventory = sLTaskInventory
        notifyDataSetChanged()
    }
}
