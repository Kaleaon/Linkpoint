// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ActiveChatsListAdapter

private static class isAnyoneOnline
{

    private boolean isAnyoneOnline;

    public View getView(LayoutInflater layoutinflater, View view, ViewGroup viewgroup)
    {
        Object obj = null;
        View view1;
        int i;
        int j;
        if (isAnyoneOnline)
        {
            i = 0x7f1001c4;
        } else
        {
            i = 0x7f1001c5;
        }
        if (isAnyoneOnline)
        {
            j = 0x7f040056;
        } else
        {
            j = 0x7f040057;
        }
        view1 = obj;
        if (view != null)
        {
            view1 = obj;
            if (view.getId() == i)
            {
                view1 = view;
            }
        }
        view = view1;
        if (view1 == null)
        {
            view = layoutinflater.inflate(j, viewgroup, false);
        }
        layoutinflater = (TextView)view.findViewById(i);
        if (isAnyoneOnline)
        {
            i = 0x7f090125;
        } else
        {
            i = 0x7f0901df;
        }
        layoutinflater.setText(i);
        return view;
    }

    public void setAnyoneOnline(boolean flag)
    {
        isAnyoneOnline = flag;
    }

    public ()
    {
        isAnyoneOnline = false;
    }
}
