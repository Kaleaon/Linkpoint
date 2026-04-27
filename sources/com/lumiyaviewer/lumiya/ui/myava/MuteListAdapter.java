// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import java.util.List;

class MuteListAdapter extends BaseAdapter
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_mutelist_2D_MuteTypeSwitchesValues[];
    private final LayoutInflater layoutInflater;
    private ImmutableList muteList;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_mutelist_2D_MuteTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_mutelist_2D_MuteTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_mutelist_2D_MuteTypeSwitchesValues;
        }
        int ai[] = new int[MuteType.values().length];
        try
        {
            ai[MuteType.AGENT.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[MuteType.BY_NAME.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[MuteType.EXTERNAL.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[MuteType.GROUP.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[MuteType.OBJECT.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_mutelist_2D_MuteTypeSwitchesValues = ai;
        return ai;
    }

    MuteListAdapter(Context context)
    {
        muteList = ImmutableList.of();
        layoutInflater = LayoutInflater.from(context);
    }

    public int getCount()
    {
        return muteList.size();
    }

    public MuteListEntry getItem(int i)
    {
        if (i >= 0 && i < muteList.size())
        {
            return (MuteListEntry)muteList.get(i);
        } else
        {
            return null;
        }
    }

    public volatile Object getItem(int i)
    {
        return getItem(i);
    }

    public long getItemId(int i)
    {
        return 0L;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        MuteListEntry mutelistentry = getItem(i);
        if (mutelistentry == null) goto _L2; else goto _L1
_L1:
        View view1;
        view1 = view;
        if (view == null)
        {
            view1 = layoutInflater.inflate(0x7f040062, viewgroup, false);
        }
        if (view1 == null) goto _L2; else goto _L3
_L3:
        ((TextView)view1.findViewById(0x7f1001e7)).setText(mutelistentry.name);
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_mutelist_2D_MuteTypeSwitchesValues()[mutelistentry.type.ordinal()];
        JVM INSTR tableswitch 1 5: default 100
    //                   1 125
    //                   2 131
    //                   3 100
    //                   4 125
    //                   5 131;
           goto _L4 _L5 _L6 _L4 _L5 _L6
_L4:
        i = 0x7f0200c4;
_L7:
        ((ImageView)view1.findViewById(0x7f1001e6)).setImageResource(i);
        SwipeDismissListViewTouchListener.restoreViewState(view1);
        return view1;
_L5:
        i = 0x7f0200c1;
        continue; /* Loop/switch isn't completed */
_L6:
        i = 0x7f0200c6;
        if (true) goto _L7; else goto _L2
_L2:
        return null;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    void setData(List list)
    {
        if (list != null)
        {
            list = ImmutableList.copyOf(list);
        } else
        {
            list = ImmutableList.of();
        }
        muteList = list;
        notifyDataSetChanged();
    }
}
