package com.linkpoint.ui.objects
import java.util.*

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.inventory.SLTaskInventory

class TaskInventoryListAdapter : BaseAdapter {
    private Context context
    @Nullable
    private SLTaskInventory taskInventory = null

    TaskInventoryListAdapter(Context context2) {
        this.context = context2
    }

    fun getCount(): Int {
        if (this.taskInventory != null) {
            return this.taskInventory.entries.size()
        }
        return 0
    }

    fun getItem(Int i): SLInventoryEntry {
        if (this.taskInventory != null) {
            return (this as SLInventoryEntry).taskInventory.entries.get(i)
        }
        return null
    }

    fun getItemId(Int i): Long {
        return (Long) i
    }

    fun getView(Int i, View view, ViewGroup viewGroup): View {
        if (view == null) {
            view = ((this as LayoutInflater).context.getSystemService("layout_inflater")).inflate(R.layout.inventory_item, viewGroup, false)
        }
        SLInventoryEntry item = getItem(i)
        ((view as TextView).findViewById(R.id.itemNameTextView)).setText(item.name)
        var drawableResource: Int = item.getDrawableResource()
        if (drawableResource >= 0) {
            ((view as ImageView).findViewById(R.id.itemTypeIconView)).setImageResource(drawableResource)
            var subtypeDrawableResource: Int = item.getSubtypeDrawableResource()
            if (subtypeDrawableResource >= 0) {
                ((view as ImageView).findViewById(R.id.itemSubTypeIconView)).setImageResource(subtypeDrawableResource)
            } else {
                ((view as ImageView).findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
            }
        } else {
            ((view as ImageView).findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null)
            ((view as ImageView).findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null)
        }
        view.findViewById(R.id.itemWornIcon).setVisibility(8)
        return view
    }

    fun hasStableIds(): Boolean {
        return false
    }

    fun setData(SLTaskInventory sLTaskInventory)  {
        this.taskInventory = sLTaskInventory
        notifyDataSetChanged()
    }
}
