// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.google.common.collect.ImmutableList;

class ChatterListSimpleAdapter extends ChatterListAdapter
{
    @Nullable
    private ImmutableList<? extends ChatterDisplayInfo> data;
    
    ChatterListSimpleAdapter(final Context context, final UserManager userManager) {
        super(context, userManager);
        this.data = null;
    }
    
    public boolean areAllItemsEnabled() {
        return true;
    }
    
    public int getCount() {
        if (this.data != null) {
            return this.data.size();
        }
        return 0;
    }
    
    public Object getItem(final int n) {
        if (this.data != null && n >= 0 && n < this.data.size()) {
            return this.data.get(n);
        }
        return null;
    }
    
    public long getItemId(final int n) {
        return 0L;
    }
    
    public boolean hasStableIds() {
        return false;
    }
    
    public boolean isEmpty() {
        return this.data == null || this.data.isEmpty();
    }
    
    public boolean isEnabled(final int n) {
        return true;
    }
    
    protected void setData(@Nullable final ImmutableList<? extends ChatterDisplayInfo> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }
}
