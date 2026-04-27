// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserPicksProfileTab

private static class <init> extends BaseAdapter
{

    private final LayoutInflater inflater;
    private AvatarPicksReply picksReply;

    public int getCount()
    {
        if (picksReply != null)
        {
            return picksReply.Data_Fields.size();
        } else
        {
            return 0;
        }
    }

    public com.lumiyaviewer.lumiya.slproto.messages.picksReply getItem(int i)
    {
        if (picksReply != null && i >= 0 && i < picksReply.Data_Fields.size())
        {
            return (com.lumiyaviewer.lumiya.slproto.messages.picksReply)picksReply.Data_Fields.get(i);
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
        return (long)i;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        View view1 = view;
        if (view == null)
        {
            view1 = inflater.inflate(0x1090003, viewgroup, false);
        }
        view = getItem(i);
        if (view != null)
        {
            ((TextView)view1.findViewById(0x1020014)).setText(SLMessage.stringFromVariableUTF(((com.lumiyaviewer.lumiya.slproto.messages.getItem) (view)).getItem));
        }
        return view1;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    void setData(AvatarPicksReply avatarpicksreply)
    {
        picksReply = avatarpicksreply;
        notifyDataSetChanged();
    }

    private (Context context)
    {
        picksReply = null;
        inflater = LayoutInflater.from(context);
    }

    inflater(Context context, inflater inflater1)
    {
        this(context);
    }
}
