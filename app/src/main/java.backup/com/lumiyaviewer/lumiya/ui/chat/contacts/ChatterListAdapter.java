package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;

abstract class ChatterListAdapter extends BaseAdapter implements ListAdapter {
    protected final Context context;
    private final LayoutInflater inflater;
    private boolean userDistanceInline = true;
    protected final UserManager userManager;
    private final ChatterItemViewBuilder viewBuilder = new ChatterItemViewBuilder();

    ChatterListAdapter(Context context2, UserManager userManager2) {
        this.context = context2;
        this.userManager = userManager2;
        this.inflater = LayoutInflater.from(context2);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        Object item = getItem(i);
        if (!(item instanceof ChatterDisplayInfo)) {
            return null;
        }
        this.viewBuilder.reset();
        ((ChatterDisplayInfo) item).buildView(this.context, this.viewBuilder, this.userManager);
        return this.viewBuilder.getView(this.inflater, view, viewGroup, this.userDistanceInline);
    }

    /* access modifiers changed from: package-private */
    public void setUserDistanceInline(boolean z) {
        this.userDistanceInline = z;
    }
}
