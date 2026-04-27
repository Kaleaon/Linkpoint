// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.view.View;
import android.widget.ExpandableListView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectListAdapter

class val.groupPosition
    implements android.view.
{

    final ObjectListAdapter this$0;
    final ExpandableListView val$expandableListView;
    final int val$groupPosition;

    public void onClick(View view)
    {
        if (view.getVisibility() != 0) goto _L2; else goto _L1
_L1:
        view.getId();
        JVM INSTR tableswitch 2131755573 2131755574: default 32
    //                   2131755573 33
    //                   2131755574 68;
           goto _L2 _L3 _L4
_L2:
        return;
_L3:
        if (android.os.T >= 14)
        {
            val$expandableListView.expandGroup(val$groupPosition, true);
            return;
        } else
        {
            val$expandableListView.expandGroup(val$groupPosition);
            return;
        }
_L4:
        val$expandableListView.collapseGroup(val$groupPosition);
        return;
    }

    ()
    {
        this$0 = final_objectlistadapter;
        val$expandableListView = expandablelistview;
        val$groupPosition = I.this;
        super();
    }
}
