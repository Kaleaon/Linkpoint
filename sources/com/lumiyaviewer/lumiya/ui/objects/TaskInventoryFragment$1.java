// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            TaskInventoryFragment, TaskInventoryListAdapter

class this._cls0
    implements com.lumiyaviewer.lumiya.react.nt._cls1
{

    final TaskInventoryFragment this$0;

    public void onData(SLTaskInventory sltaskinventory)
    {
        TaskInventoryFragment._2D_set0(TaskInventoryFragment.this, sltaskinventory);
        View view = getView();
        if (view != null)
        {
            android.widget.ListAdapter listadapter = ((ListView)view.findViewById(0x7f10028c)).getAdapter();
            if (listadapter instanceof TaskInventoryListAdapter)
            {
                ((TaskInventoryListAdapter)listadapter).setData(sltaskinventory);
            }
            ((TextView)view.findViewById(0x7f10028e)).setText(0x7f090218);
            view.findViewById(0x7f10028d).setVisibility(8);
        }
    }

    public volatile void onData(Object obj)
    {
        onData((SLTaskInventory)obj);
    }

    ()
    {
        this$0 = TaskInventoryFragment.this;
        super();
    }
}
