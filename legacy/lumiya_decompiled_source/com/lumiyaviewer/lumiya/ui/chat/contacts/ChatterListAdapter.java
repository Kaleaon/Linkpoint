package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
/* loaded from: classes.dex */
abstract class ChatterListAdapter extends BaseAdapter implements ListAdapter {
    protected final Context context;
    private final LayoutInflater inflater;
    protected final UserManager userManager;
    private final ChatterItemViewBuilder viewBuilder = new ChatterItemViewBuilder();
    private boolean userDistanceInline = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterListAdapter(Context context, UserManager userManager) {
        this.context = context;
        this.userManager = userManager;
        this.inflater = LayoutInflater.from(context);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        Object item = getItem(i);
        if (item instanceof ChatterDisplayInfo) {
            this.viewBuilder.reset();
            ((ChatterDisplayInfo) item).buildView(this.context, this.viewBuilder, this.userManager);
            return this.viewBuilder.getView(this.inflater, view, viewGroup, this.userDistanceInline);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUserDistanceInline(boolean z) {
        this.userDistanceInline = z;
    }
}
