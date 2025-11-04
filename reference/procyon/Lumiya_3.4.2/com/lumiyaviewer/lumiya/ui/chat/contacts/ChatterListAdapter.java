// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import android.view.ViewGroup;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.ListAdapter;
import android.widget.BaseAdapter;

abstract class ChatterListAdapter extends BaseAdapter implements ListAdapter
{
    protected final Context context;
    private final LayoutInflater inflater;
    private boolean userDistanceInline;
    protected final UserManager userManager;
    private final ChatterItemViewBuilder viewBuilder;
    
    ChatterListAdapter(final Context context, final UserManager userManager) {
        this.viewBuilder = new ChatterItemViewBuilder();
        this.userDistanceInline = true;
        this.context = context;
        this.userManager = userManager;
        this.inflater = LayoutInflater.from(context);
    }
    
    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        final Object item = this.getItem(n);
        if (item instanceof ChatterDisplayInfo) {
            this.viewBuilder.reset();
            ((ChatterDisplayInfo)item).buildView(this.context, this.viewBuilder, this.userManager);
            return this.viewBuilder.getView(this.inflater, view, viewGroup, this.userDistanceInline);
        }
        return null;
    }
    
    void setUserDistanceInline(final boolean userDistanceInline) {
        this.userDistanceInline = userDistanceInline;
    }
}
