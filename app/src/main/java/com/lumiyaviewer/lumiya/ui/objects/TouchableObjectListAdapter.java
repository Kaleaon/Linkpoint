package com.lumiyaviewer.lumiya.ui.objects;
import java.util.*;

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

class TouchableObjectListAdapter extends BaseAdapter {
    private final Context context;
    @Nonnull
    private ImmutableList<SLObjectInfo> objects = ImmutableList.of();

    TouchableObjectListAdapter(Context context2) {
        this.context = context2;
    }

    public int getCount() {
        return this.objects.size();
    }

    public SLObjectInfo getItem(int i) {
        if (i < 0 || i >= this.objects.size()) {
            return null;
        }
        return (SLObjectInfo) this.objects.get(i);
    }

    public long getItemId(int i) {
        SLObjectInfo item = getItem(i);
        if (item != null) {
            return (long) item.localID;
        }
        return -1;
    }

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

    public boolean hasStableIds() {
        return true;
    }

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
