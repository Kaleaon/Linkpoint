// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.outfits;

import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;
import android.support.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import android.view.LayoutInflater;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import android.widget.BaseAdapter;

class CurrentOutfitAdapter extends BaseAdapter implements DismissableAdapter
{
    @Nullable
    private SLAvatarAppearance avatarAppearance;
    private final LayoutInflater inflater;
    @NonNull
    private ImmutableList<SLAvatarAppearance.WornItem> wornItems;
    
    CurrentOutfitAdapter(final Context context) {
        this.wornItems = ImmutableList.of();
        this.inflater = LayoutInflater.from(context);
    }
    
    public boolean canDismiss(final int n) {
        boolean canTakeItemOff = false;
        final SLAvatarAppearance.WornItem item = this.getItem(n);
        if (item == null || this.avatarAppearance == null) {
            return false;
        }
        if (item.getWornOn() != null) {
            if (!item.getWornOn().isBodyPart()) {
                canTakeItemOff = this.avatarAppearance.canTakeItemOff(item.getWornOn());
            }
            return canTakeItemOff;
        }
        return this.avatarAppearance.canDetachItem(item);
    }
    
    public int getCount() {
        return this.wornItems.size();
    }
    
    public SLAvatarAppearance.WornItem getItem(final int n) {
        if (n >= 0 && n < this.wornItems.size()) {
            return this.wornItems.get(n);
        }
        return null;
    }
    
    public long getItemId(final int n) {
        return n;
    }
    
    public int getItemViewType(final int n) {
        return 0;
    }
    
    public View getView(int visibility, View inflate, final ViewGroup viewGroup) {
        final View view = null;
        if (inflate != null && inflate.getId() != 2131755593) {
            inflate = view;
        }
        if (inflate == null) {
            inflate = this.inflater.inflate(2130968702, viewGroup, false);
        }
        final SLAvatarAppearance.WornItem wornItem = this.wornItems.get(visibility);
        ((TextView)inflate.findViewById(2131755455)).setText((CharSequence)wornItem.getName());
        if (wornItem.getWornOn() != null) {
            ((ImageView)inflate.findViewById(2131755453)).setImageResource(2130837695);
            inflate.findViewById(2131755594).setVisibility(8);
        }
        else {
            ((ImageView)inflate.findViewById(2131755453)).setImageResource(2130837702);
            final View viewById = inflate.findViewById(2131755594);
            if (wornItem.getIsTouchable()) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
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
    
    public void onDismiss(final int n) {
        final SLAvatarAppearance.WornItem item = this.getItem(n);
        if (item != null && this.avatarAppearance != null) {
            if (item.getWornOn() != null) {
                this.avatarAppearance.TakeItemOff(item.itemID());
            }
            else {
                this.avatarAppearance.DetachItem(item);
            }
        }
    }
    
    public void setAvatarAppearance(@Nullable final SLAvatarAppearance avatarAppearance) {
        this.avatarAppearance = avatarAppearance;
    }
    
    public void setData(ImmutableList<SLAvatarAppearance.WornItem> of) {
        if (of == null) {
            of = ImmutableList.of();
        }
        this.wornItems = of;
        this.notifyDataSetChanged();
    }
}
