// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.accounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.accounts:
//            ManageAccountsActivity

private static class gridList extends ArrayAdapter
{

    private GridList gridList;

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Object obj = (LayoutInflater)getContext().getSystemService("layout_inflater");
        View view1 = view;
        if (view == null)
        {
            view1 = ((LayoutInflater) (obj)).inflate(0x7f04001c, viewgroup, false);
        }
        view = (TextView)view1.findViewById(0x7f1000b9);
        viewgroup = (TextView)view1.findViewById(0x7f1000ba);
        obj = getItem(i);
        if (obj != null)
        {
            obj = (getItem)obj;
            com.lumiyaviewer.lumiya.ui.grids.er er = gridList.getGridByUUID(((gridList) (obj)).gridList());
            view.setText(((gridList) (obj)).gridList());
            if (er != null)
            {
                view = er.gridList();
            } else
            {
                view = "";
            }
            viewgroup.setText(view);
        }
        return view1;
    }

    void updateGridList()
    {
        gridList.loadGrids();
    }

    void updateList()
    {
        super.notifyDataSetChanged();
    }

    (Context context, List list)
    {
        super(context, 0x7f04001c, list);
        gridList = new GridList(context);
    }
}
