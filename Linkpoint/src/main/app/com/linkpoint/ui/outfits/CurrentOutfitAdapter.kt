package com.linkpoint.ui.outfits
import java.util.*

import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.Nullable
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

class CurrentOutfitAdapter : BaseAdapter : DismissableAdapter {
    @Nullable
    private SLAvatarAppearance avatarAppearance
    private LayoutInflater inflater
    @NonNull
    private ImmutableList<SLAvatarAppearance.WornItem> wornItems = ImmutableList.of()

    CurrentOutfitAdapter(Context context) {
        this.inflater = LayoutInflater.from(context)
    }

    fun canDismiss(Int i): Boolean {
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

    fun getCount(): Int {
        return this.wornItems.size()
    }

    SLAvatarAppearance.WornItem getItem(Int i) {
        if (i < 0 || i >= this.wornItems.size()) {
            return null
        }
        return (SLAvatarAppearance.WornItem) this.wornItems.get(i)
    }

    fun getItemId(Int i): Long {
        return (Long) i
    }

    fun getItemViewType(Int i): Int {
        return 0
    }

    fun getView(Int i, View view, ViewGroup viewGroup): View {
        View view2 = null
        if (view == null || view.getId() == R.id.outfitItemLayout) {
            view2 = view
        }
        View inflate = view2 == null ? this.inflater.inflate(R.layout.outfit_item, viewGroup, false) : view2
        SLAvatarAppearance.WornItem wornItem = (SLAvatarAppearance.WornItem) this.wornItems.get(i)
        ((inflate as TextView).findViewById(R.id.itemNameTextView)).setText(wornItem.getName())
        if (wornItem.getWornOn() != null) {
            ((inflate as ImageView).findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_clothes)
            inflate.findViewById(R.id.itemTouchableIcon).setVisibility(8)
        } else {
            ((inflate as ImageView).findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_object)
            inflate.findViewById(R.id.itemTouchableIcon).setVisibility(wornItem.getIsTouchable() ? 0 : 8)
        }
        SwipeDismissListViewTouchListener.restoreViewState(inflate)
        return inflate
    }

    fun hasStableIds(): Boolean {
        return false
    }

    fun isEmpty(): Boolean {
        return this.wornItems.isEmpty()
    }

    fun onDismiss(Int i): Unit {
        SLAvatarAppearance.WornItem item = getItem(i)
        if (item != null && this.avatarAppearance != null) {
            if (item.getWornOn() != null) {
                this.avatarAppearance.TakeItemOff(item.itemID())
            } else {
                this.avatarAppearance.DetachItem(item)
            }
        }
    }

    fun setAvatarAppearance(@Nullable SLAvatarAppearance sLAvatarAppearance): Unit {
        this.avatarAppearance = sLAvatarAppearance
    }

    fun setData(ImmutableList<SLAvatarAppearance.WornItem> immutableList): Unit {
        if (immutableList == null) {
            immutableList = ImmutableList.of()
        }
        this.wornItems = immutableList
        notifyDataSetChanged()
    }
}
