package com.lumiyaviewer.lumiya.ui.outfits;
import java.util.*;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;

class CurrentOutfitAdapter extends BaseAdapter implements DismissableAdapter {
    @Nullable
    private SLAvatarAppearance avatarAppearance;
    private final LayoutInflater inflater;
    @NonNull
    private ImmutableList<SLAvatarAppearance.WornItem> wornItems = ImmutableList.of();

    CurrentOutfitAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public boolean canDismiss(int i) {
        SLAvatarAppearance.WornItem item = getItem(i);
        if (item == null || this.avatarAppearance == null) {
            return false;
        }
        if (item.getWornOn() == null) {
            return this.avatarAppearance.canDetachItem(item);
        }
        if (!item.getWornOn().isBodyPart()) {
            return this.avatarAppearance.canTakeItemOff(item.getWornOn());
        }
        return false;
    }

    public int getCount() {
        return this.wornItems.size();
    }

    public SLAvatarAppearance.WornItem getItem(int i) {
        if (i < 0 || i >= this.wornItems.size()) {
            return null;
        }
        return (SLAvatarAppearance.WornItem) this.wornItems.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = null;
        if (view == null || view.getId() == R.id.outfitItemLayout) {
            view2 = view;
        }
        View inflate = view2 == null ? this.inflater.inflate(R.layout.outfit_item, viewGroup, false) : view2;
        SLAvatarAppearance.WornItem wornItem = (SLAvatarAppearance.WornItem) this.wornItems.get(i);
        ((TextView) inflate.findViewById(R.id.itemNameTextView)).setText(wornItem.getName());
        if (wornItem.getWornOn() != null) {
            ((ImageView) inflate.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_clothes);
            inflate.findViewById(R.id.itemTouchableIcon).setVisibility(8);
        } else {
            ((ImageView) inflate.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_object);
            inflate.findViewById(R.id.itemTouchableIcon).setVisibility(wornItem.getIsTouchable() ? 0 : 8);
        }
        SwipeDismissListViewTouchListener.restoreViewState(inflate);
        return inflate;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isEmpty() {
        return this.wornItems.isEmpty();
    }

    public void onDismiss(int i) {
        SLAvatarAppearance.WornItem item = getItem(i);
        if (item != null && this.avatarAppearance != null) {
            if (item.getWornOn() != null) {
                this.avatarAppearance.TakeItemOff(item.itemID());
            } else {
                this.avatarAppearance.DetachItem(item);
            }
        }
    }

    public void setAvatarAppearance(@Nullable SLAvatarAppearance sLAvatarAppearance) {
        this.avatarAppearance = sLAvatarAppearance;
    }

    public void setData(ImmutableList<SLAvatarAppearance.WornItem> immutableList) {
        if (immutableList == null) {
            immutableList = ImmutableList.of();
        }
        this.wornItems = immutableList;
        notifyDataSetChanged();
    }
}
