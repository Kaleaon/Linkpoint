package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import javax.annotation.Nullable;

class ChatterListSimpleAdapter extends ChatterListAdapter {
    @Nullable
    private ImmutableList<? extends ChatterDisplayInfo> data = null;

    ChatterListSimpleAdapter(Context context, UserManager userManager) {
        super(context, userManager);
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

    public Object getItem(int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return null;
        }
        return this.data.get(i);
    }

    public long getItemId(int i) {
        return 0;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isEmpty() {
        if (this.data != null) {
            return this.data.isEmpty();
        }
        return true;
    }

    public boolean isEnabled(int i) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void setData(@Nullable ImmutableList<? extends ChatterDisplayInfo> immutableList) {
        this.data = immutableList;
        notifyDataSetChanged();
    }
}
