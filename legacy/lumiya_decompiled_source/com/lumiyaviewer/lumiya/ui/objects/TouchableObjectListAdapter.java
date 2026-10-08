package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
class TouchableObjectListAdapter extends BaseAdapter {
    private final Context context;
    @Nonnull
    private ImmutableList<SLObjectInfo> objects = ImmutableList.of();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TouchableObjectListAdapter(Context context) {
        this.context = context;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.objects.size();
    }

    @Override // android.widget.Adapter
    public SLObjectInfo getItem(int i) {
        if (i < 0 || i >= this.objects.size()) {
            return null;
        }
        return this.objects.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        SLObjectInfo item = getItem(i);
        if (item != null) {
            return item.localID;
        }
        return -1L;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = null;
        SLObjectInfo item = getItem(i);
        if (item == null) {
            return null;
        }
        if (view == null || view.getId() == R.id.touchable_object_list_item) {
            view2 = view;
        }
        View inflate = view2 == null ? ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.touchable_object_list_item, viewGroup, false) : view2;
        ((TextView) inflate.findViewById(R.id.touchable_objectNameTextView)).setText(item.getName());
        inflate.findViewById(R.id.touchable_touchIconView).setVisibility(item.isTouchable() ? 0 : 4);
        return inflate;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean isEmpty() {
        return this.objects.isEmpty();
    }

    public void setData(@Nullable ImmutableList<SLObjectInfo> immutableList) {
        if (immutableList == null) {
            immutableList = ImmutableList.of();
        }
        this.objects = immutableList;
        notifyDataSetChanged();
    }
}
