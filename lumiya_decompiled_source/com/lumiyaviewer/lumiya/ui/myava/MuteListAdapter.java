package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
class MuteListAdapter extends BaseAdapter {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f461xafabc05e = null;
    private final LayoutInflater layoutInflater;
    @Nonnull
    private ImmutableList<MuteListEntry> muteList = ImmutableList.of();

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m657x1327cd02() {
        if (f461xafabc05e != null) {
            return f461xafabc05e;
        }
        int[] iArr = new int[MuteType.valuesCustom().length];
        try {
            iArr[MuteType.AGENT.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[MuteType.BY_NAME.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[MuteType.EXTERNAL.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[MuteType.GROUP.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[MuteType.OBJECT.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        f461xafabc05e = iArr;
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MuteListAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.muteList.size();
    }

    @Override // android.widget.Adapter
    public MuteListEntry getItem(int i) {
        if (i < 0 || i >= this.muteList.size()) {
            return null;
        }
        return this.muteList.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        int i2;
        MuteListEntry item = getItem(i);
        if (item != null) {
            if (view == null) {
                view = this.layoutInflater.inflate(R.layout.mute_list_item, viewGroup, false);
            }
            if (view != null) {
                ((TextView) view.findViewById(R.id.muteName)).setText(item.name);
                switch (m657x1327cd02()[item.type.ordinal()]) {
                    case 1:
                    case 4:
                        i2 = R.drawable.inv_human;
                        break;
                    case 2:
                    case 5:
                        i2 = R.drawable.inv_object;
                        break;
                    case 3:
                    default:
                        i2 = R.drawable.inv_link;
                        break;
                }
                ((ImageView) view.findViewById(R.id.muteTypeIcon)).setImageResource(i2);
                SwipeDismissListViewTouchListener.restoreViewState(view);
                return view;
            }
        }
        return null;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setData(@Nullable List<MuteListEntry> list) {
        this.muteList = list != null ? ImmutableList.copyOf((Collection) list) : ImmutableList.of();
        notifyDataSetChanged();
    }
}
