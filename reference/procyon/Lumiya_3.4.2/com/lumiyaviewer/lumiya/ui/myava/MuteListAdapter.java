// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import java.util.Collection;
import javax.annotation.Nullable;
import java.util.List;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.google.common.collect.ImmutableList;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

class MuteListAdapter extends BaseAdapter
{
    private final LayoutInflater layoutInflater;
    @Nonnull
    private ImmutableList<MuteListEntry> muteList;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues() {
        if (MuteListAdapter.-com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues != null) {
            return MuteListAdapter.-com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues = new int[MuteType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues[MuteType.AGENT.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues[MuteType.BY_NAME.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues[MuteType.EXTERNAL.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues[MuteType.GROUP.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues[MuteType.OBJECT.ordinal()] = 5;
                                return -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {}
            }
            catch (final NoSuchFieldError noSuchFieldError5) {
                continue;
            }
            break;
        }
    }
    
    MuteListAdapter(final Context context) {
        this.muteList = ImmutableList.of();
        this.layoutInflater = LayoutInflater.from(context);
    }
    
    public int getCount() {
        return this.muteList.size();
    }
    
    public MuteListEntry getItem(final int n) {
        if (n >= 0 && n < this.muteList.size()) {
            return this.muteList.get(n);
        }
        return null;
    }
    
    public long getItemId(final int n) {
        return 0L;
    }
    
    public View getView(int imageResource, final View view, final ViewGroup viewGroup) {
        final MuteListEntry item = this.getItem(imageResource);
        if (item != null) {
            View inflate;
            if ((inflate = view) == null) {
                inflate = this.layoutInflater.inflate(2130968674, viewGroup, false);
            }
            if (inflate != null) {
                ((TextView)inflate.findViewById(2131755495)).setText((CharSequence)item.name);
                switch (-getcom-lumiyaviewer-lumiya-slproto-modules-mutelist-MuteTypeSwitchesValues()[item.type.ordinal()]) {
                    default: {
                        imageResource = 2130837700;
                        break;
                    }
                    case 1:
                    case 4: {
                        imageResource = 2130837697;
                        break;
                    }
                    case 2:
                    case 5: {
                        imageResource = 2130837702;
                        break;
                    }
                }
                ((ImageView)inflate.findViewById(2131755494)).setImageResource(imageResource);
                SwipeDismissListViewTouchListener.restoreViewState(inflate);
                return inflate;
            }
        }
        return null;
    }
    
    public boolean hasStableIds() {
        return false;
    }
    
    void setData(@Nullable final List<MuteListEntry> list) {
        ImmutableList<Object> muteList;
        if (list != null) {
            muteList = (ImmutableList<Object>)ImmutableList.copyOf((Collection<? extends MuteListEntry>)list);
        }
        else {
            muteList = (ImmutableList<Object>)ImmutableList.of();
        }
        this.muteList = (ImmutableList<MuteListEntry>)muteList;
        this.notifyDataSetChanged();
    }
}
