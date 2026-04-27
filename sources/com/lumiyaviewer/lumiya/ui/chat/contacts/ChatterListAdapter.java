// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ChatterItemViewBuilder

abstract class ChatterListAdapter extends BaseAdapter
    implements ListAdapter
{

    protected final Context context;
    private final LayoutInflater inflater;
    private boolean userDistanceInline;
    protected final UserManager userManager;
    private final ChatterItemViewBuilder viewBuilder = new ChatterItemViewBuilder();

    ChatterListAdapter(Context context1, UserManager usermanager)
    {
        userDistanceInline = true;
        context = context1;
        userManager = usermanager;
        inflater = LayoutInflater.from(context1);
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Object obj = getItem(i);
        if (obj instanceof ChatterDisplayInfo)
        {
            viewBuilder.reset();
            ((ChatterDisplayInfo)obj).buildView(context, viewBuilder, userManager);
            return viewBuilder.getView(inflater, view, viewgroup, userDistanceInline);
        } else
        {
            return null;
        }
    }

    void setUserDistanceInline(boolean flag)
    {
        userDistanceInline = flag;
    }
}
