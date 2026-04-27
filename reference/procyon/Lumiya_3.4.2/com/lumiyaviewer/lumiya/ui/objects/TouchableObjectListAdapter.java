// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import javax.annotation.Nullable;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.google.common.collect.ImmutableList;
import android.content.Context;
import android.widget.BaseAdapter;

class TouchableObjectListAdapter extends BaseAdapter
{
    private final Context context;
    @Nonnull
    private ImmutableList<SLObjectInfo> objects;
    
    TouchableObjectListAdapter(final Context context) {
        this.objects = ImmutableList.of();
        this.context = context;
    }
    
    public int getCount() {
        return this.objects.size();
    }
    
    public SLObjectInfo getItem(final int n) {
        if (n >= 0 && n < this.objects.size()) {
            return this.objects.get(n);
        }
        return null;
    }
    
    public long getItemId(final int n) {
        final SLObjectInfo item = this.getItem(n);
        long n2;
        if (item != null) {
            n2 = item.localID;
        }
        else {
            n2 = -1L;
        }
        return n2;
    }
    
    public View getView(int visibility, View inflate, final ViewGroup viewGroup) {
        final View view = null;
        final SLObjectInfo item = this.getItem(visibility);
        if (item == null) {
            return null;
        }
        if (inflate != null && inflate.getId() != 2131755673) {
            inflate = view;
        }
        if (inflate == null) {
            inflate = ((LayoutInflater)this.context.getSystemService("layout_inflater")).inflate(2130968750, viewGroup, false);
        }
        ((TextView)inflate.findViewById(2131755674)).setText((CharSequence)item.getName());
        final View viewById = inflate.findViewById(2131755675);
        if (item.isTouchable()) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        viewById.setVisibility(visibility);
        return inflate;
    }
    
    public boolean hasStableIds() {
        return true;
    }
    
    public boolean isEmpty() {
        return this.objects.isEmpty();
    }
    
    public void setData(@Nullable ImmutableList<SLObjectInfo> of) {
        if (of == null) {
            of = ImmutableList.of();
        }
        this.objects = of;
        this.notifyDataSetChanged();
    }
}
