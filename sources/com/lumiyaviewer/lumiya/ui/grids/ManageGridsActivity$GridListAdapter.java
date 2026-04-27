// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.grids:
//            ManageGridsActivity

private static class  extends ArrayAdapter
{

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Object obj = LayoutInflater.from(getContext());
        View view1 = view;
        if (view == null)
        {
            view1 = ((LayoutInflater) (obj)).inflate(0x7f040040, viewgroup, false);
        }
        view = (TextView)view1.findViewById(0x7f1000ba);
        viewgroup = (TextView)view1.findViewById(0x7f100161);
        obj = getItem(i);
        if (obj != null)
        {
            obj = (getItem)obj;
            view.setText(((getItem) (obj)).getItem());
            viewgroup.setText(((getItem) (obj)).getItem());
            view = view1.findViewById(0x7f100162);
            if (((getItem) (obj)).getItem())
            {
                i = 0;
            } else
            {
                i = 4;
            }
            view.setVisibility(i);
        }
        return view1;
    }

    void updateList()
    {
        super.notifyDataSetChanged();
    }

    (Context context, List list)
    {
        super(context, 0x7f040040, list);
    }
}
