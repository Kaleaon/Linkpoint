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

    public Boolean canDismiss(Int i) {
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

    public Int getCount() {
        return this.wornItems.size()
    }

    public SLAvatarAppearance.WornItem getItem(Int i) {
        if (i < 0 || i >= this.wornItems.size()) {
            return null
        }
        return (SLAvatarAppearance.WornItem) this.wornItems.get(i)
    }

    public Long getItemId(Int i) {
        return (Long) i
    }

    public Int getItemViewType(Int i) {
        return 0
    }

    public View getView(Int i, View view, ViewGroup viewGroup) {
        View view2 = null
        if (view == null || view.getId() == R.id.outfitItemLayout) {
            view2 = view
        }
        View inflate = view2 == null ? this.inflater.inflate(R.layout.outfit_item, viewGroup, false) : view2
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

    public Boolean hasStableIds() {
        return false
    }

    public Boolean isEmpty() {
        return this.wornItems.isEmpty()
    }

    fun onDismiss(Int i) {
        SLAvatarAppearance.WornItem item = getItem(i)
        if (item != null && this.avatarAppearance != null) {
            if (item.getWornOn() != null) {
                this.avatarAppearance.TakeItemOff(item.itemID())
            } else {
                this.avatarAppearance.DetachItem(item)
            }
        }
    }

    fun setAvatarAppearance(SLAvatarAppearance sLAvatarAppearance) {
        this.avatarAppearance = sLAvatarAppearance
    }

    fun setData(ImmutableList<SLAvatarAppearance.WornItem> immutableList) {
        if (immutableList == null) {
            immutableList = ImmutableList.of()
        }
        this.wornItems = immutableList
        notifyDataSetChanged()
    }
}
