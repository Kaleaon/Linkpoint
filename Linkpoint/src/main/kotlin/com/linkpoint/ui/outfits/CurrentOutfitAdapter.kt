package com.linkpoint.ui.outfits
import java.util.*

import android.content.Context
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.linkpoint.R
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.ui.common.DismissableAdapter
import com.linkpoint.ui.common.SwipeDismissListViewTouchListener

class CurrentOutfitAdapter : BaseAdapter(), DismissableAdapter {
    private SLAvatarAppearance avatarAppearance
    private val LayoutInflater inflater
    private ImmutableList<SLAvatarAppearance.WornItem> wornItems = ImmutableList.of()

    CurrentOutfitAdapter(Context context) {
        this.inflater = LayoutInflater.from(context)
    }

     public fun canDismiss(i: Int): Boolean {
        SLAvatarAppearance.WornItem item = getItem(i)
        if (item == null || this.avatarAppearance == null) {
            return false
        }
        if (item.getWornOn() == null) {
            return this.avatarAppearance.canDetachItem(item)
        }
        if (!item.getWornOn().isBodyPart()) {
            return this.avatarAppearance.canTakeItemOff(item.getWornOn())
        }
        return false
    }

     public fun getCount(): Int {
        return this.wornItems.size()
    }

    public SLAvatarAppearance.WornItem getItem(Int i) {
        if (i < 0 || i >= this.wornItems.size()) {
            return null
        }
        return (SLAvatarAppearance.WornItem) this.wornItems.get(i)
    }

     public fun getItemId(i: Int): Long {
        return (Long) i
    }

     public fun getItemViewType(i: Int): Int {
        return 0
    }

     public fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        val view2: View = null
        if (view == null || view.getId() == R.id.outfitItemLayout) {
            view2 = view
        }
        val inflate: View = view2 == null ? this.inflater.inflate(R.layout.outfit_item, viewGroup, false) : view2
        SLAvatarAppearance.WornItem wornItem = (SLAvatarAppearance.WornItem) this.wornItems.get(i)
        ((TextView) inflate.findViewById(R.id.itemNameTextView)).setText(wornItem.getName())
        if (wornItem.getWornOn() != null) {
            ((ImageView) inflate.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_clothes)
            inflate.findViewById(R.id.itemTouchableIcon).setVisibility(8)
        } else {
            ((ImageView) inflate.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_object)
            inflate.findViewById(R.id.itemTouchableIcon).setVisibility(wornItem.getIsTouchable() ? 0 : 8)
        }
        SwipeDismissListViewTouchListener.restoreViewState(inflate)
        return inflate
    }

     public fun hasStableIds(): Boolean {
        return false
    }

     public fun isEmpty(): Boolean {
        return this.wornItems.isEmpty()
    }

    fun onDismiss(i: Int) {
        SLAvatarAppearance.WornItem item = getItem(i)
        if (item != null && this.avatarAppearance != null) {
            if (item.getWornOn() != null) {
                this.avatarAppearance.TakeItemOff(item.itemID())
            } else {
                this.avatarAppearance.DetachItem(item)
            }
        }
    }

    fun setAvatarAppearance(sLAvatarAppearance: SLAvatarAppearance) {
        this.avatarAppearance = sLAvatarAppearance
    }

    fun setData(immutableList: ImmutableList<SLAvatarAppearance.WornItem>) {
        if (immutableList == null) {
            immutableList = ImmutableList.of()
        }
        this.wornItems = immutableList
        notifyDataSetChanged()
    }
}
