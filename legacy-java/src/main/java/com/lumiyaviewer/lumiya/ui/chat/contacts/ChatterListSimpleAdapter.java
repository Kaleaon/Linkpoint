package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
class ChatterListSimpleAdapter extends ChatterListAdapter {

    @Nullable
    private ImmutableList<? extends ChatterDisplayInfo> data;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterListSimpleAdapter(Context context, UserManager userManager) {
        super(context, userManager);
        this.data = null;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (this.data != null) {
            return this.data.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return null;
        }
        return this.data.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean isEmpty() {
        if (this.data != null) {
            return this.data.isEmpty();
        }
        return true;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int i) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setData(@Nullable ImmutableList<? extends ChatterDisplayInfo> immutableList) {
        this.data = immutableList;
        notifyDataSetChanged();
    }
}
