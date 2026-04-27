// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objpopup:
//            ObjectPopupsFragment, ObjectPopupsAdapter

class back extends android.support.v7.widget.helper.allback
{

    final ObjectPopupsFragment this$0;

    public boolean onMove(RecyclerView recyclerview, android.support.v7.widget. , android.support.v7.widget. 1)
    {
        return false;
    }

    public void onSwiped(android.support.v7.widget. , int i)
    {
        UserManager usermanager = ObjectPopupsFragment._2D_wrap0(ObjectPopupsFragment.this);
        Object obj = getView();
        if (obj != null && usermanager != null)
        {
            obj = (RecyclerView)((View) (obj)).findViewById(0x7f10023f);
            if (obj != null)
            {
                obj = ((RecyclerView) (obj)).getAdapter();
                if (obj != null)
                {
                    i = .getAdapterPosition();
                    if (obj instanceof ObjectPopupsAdapter)
                    {
                         = (SLChatEvent)((ObjectPopupsAdapter)obj).getObject(i);
                        usermanager.getObjectPopupsManager().cancelObjectPopup();
                    }
                }
            }
        }
    }

    anager(int i, int j)
    {
        this$0 = ObjectPopupsFragment.this;
        super(i, j);
    }
}
